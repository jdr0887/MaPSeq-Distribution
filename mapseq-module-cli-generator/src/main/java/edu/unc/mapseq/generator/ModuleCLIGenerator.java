package edu.unc.mapseq.generator;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.renci.generator.AbstractGenerator;
import org.renci.generator.ReflectionManager;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.soap.SOAPDAOManager;
import edu.unc.mapseq.module.DryRunJobObserver;
import edu.unc.mapseq.module.ModuleExecutor;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.PersistantJobObserver;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.Ignore;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.OutputArgument;

/**
 * 
 * @author jdr0887
 * 
 */
public class ModuleCLIGenerator extends AbstractGenerator {

    protected List<Class<?>> classList;

    protected String pkg, srcDir;

    public ModuleCLIGenerator(List<Class<?>> classList, String pkg, String srcDir) {
        super();
        this.classList = classList;
        this.pkg = pkg;
        this.srcDir = srcDir;
    }

    @Override
    public void run() {

        JCodeModel codeModel;

        for (Class<?> clazz : classList) {

            try {

                codeModel = new JCodeModel();
                String newClass = String.format("%s.%sCLI", clazz.getPackage().toString().replace("package ", ""),
                        clazz.getSimpleName());
                System.out.println("Generating new class: " + newClass);
                JDefinedClass cliClass = codeModel._class(newClass);

                JClass runnableJClass = codeModel.ref(Runnable.class);
                JClass callableJClass = codeModel.ref(Callable.class);
                JClass moduleOutputJClass = codeModel.ref(ModuleOutput.class);
                JClass helpFormatterJClass = codeModel.ref(HelpFormatter.class);
                JClass applicationJClass = codeModel.ref(clazz);
                JClass optionsJClass = codeModel.ref(Options.class);
                JClass stringJClass = codeModel.ref(String.class);

                // cliClass._implements(runnableJClass);
                cliClass._implements(callableJClass.narrow(moduleOutputJClass));

                Field[] fieldArray = clazz.getDeclaredFields();
                for (Field field : fieldArray) {
                    if (field.isAnnotationPresent(InputArgument.class)
                            || field.isAnnotationPresent(OutputArgument.class)) {
                        JFieldVar fieldFieldVar = cliClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, stringJClass,
                                field.getName().toUpperCase());
                        fieldFieldVar.init(JExpr.lit("--" + field.getName()));
                    }
                }

                JFieldVar appFieldVar = cliClass.field(JMod.PRIVATE, applicationJClass, "app");

                JFieldVar helpFormatterVar = cliClass.field(JMod.PRIVATE | JMod.STATIC | JMod.FINAL,
                        helpFormatterJClass, "helpFormatter");
                helpFormatterVar.init(JExpr._new(helpFormatterJClass));

                JFieldVar cliOptionsFieldVar = cliClass.field(JMod.PRIVATE | JMod.STATIC | JMod.FINAL, optionsJClass,
                        "cliOptions");
                cliOptionsFieldVar.init(JExpr._new(optionsJClass));

                JMethod constructorMethod = cliClass.constructor(JMod.PUBLIC);
                JBlock constructorMethodBlock = constructorMethod.body();
                constructorMethodBlock.directStatement("super();");

                constructorMethod = cliClass.constructor(JMod.PUBLIC);
                JVar appVar = constructorMethod.param(applicationJClass, "app");
                constructorMethodBlock = constructorMethod.body();
                constructorMethodBlock.directStatement("super();");
                constructorMethodBlock.assign(JExpr._this().ref(appFieldVar), appVar);

                buildRun(clazz, codeModel, cliClass, appFieldVar, cliOptionsFieldVar, helpFormatterVar);
                buildMain(clazz, codeModel, cliClass, cliOptionsFieldVar, helpFormatterVar);
                File srcOutputDir = new File(this.srcDir);
                srcOutputDir.mkdirs();
                writeJavaFile(srcOutputDir, codeModel);

            } catch (JClassAlreadyExistsException e) {
                e.printStackTrace();
            }

        }

    }

    private void buildRun(Class<?> clazz, JCodeModel codeModel, JDefinedClass cliClass, JFieldVar appFieldVar,
            JFieldVar cliOptionsFieldVar, JFieldVar helpFormatterVar) {

        JClass mapseqDAOBeanServiceJClass = codeModel.ref(MaPSeqDAOBeanService.class);
        JClass soapDAOManagerJClass = codeModel.ref(SOAPDAOManager.class);
        JClass moduleExecutorJClass = codeModel.ref(ModuleExecutor.class);
        JClass moduleOutputJClass = codeModel.ref(ModuleOutput.class);
        JClass executorsJClass = codeModel.ref(Executors.class);
        JClass executorServiceJClass = codeModel.ref(ExecutorService.class);
        JClass futureJClass = codeModel.ref(Future.class);
        JClass longJClass = codeModel.ref(Long.class);
        JClass exceptionJClass = codeModel.ref(Exception.class);
        JClass systemJClass = codeModel.ref(System.class);
        JClass timeUnitJClass = codeModel.ref(TimeUnit.class);
        JClass dryRunObserverJClass = codeModel.ref(DryRunJobObserver.class);
        JClass updateJobObserverJClass = codeModel.ref(PersistantJobObserver.class);

        JMethod mainMethod = cliClass.method(JMod.PUBLIC, moduleOutputJClass, "call");
        mainMethod._throws(exceptionJClass);

        JBlock mainMethodBlock = mainMethod.body();

        JVar moduleOutputVar = mainMethodBlock.decl(moduleOutputJClass, "output");
        moduleOutputVar.init(JExpr._null());

        JVar moduleExecutorVar = mainMethodBlock.decl(moduleExecutorJClass, "moduleExecutor");
        moduleExecutorVar.init(JExpr._new(moduleExecutorJClass));

        mainMethodBlock.add(moduleExecutorVar.invoke("setModule").arg(appFieldVar));

        JConditional dryRunConditional = mainMethodBlock._if(appFieldVar.invoke("getDryRun"));
        JBlock dryRunConditionalBlock = dryRunConditional._then();
        dryRunConditionalBlock.add(moduleExecutorVar.invoke("addObserver").arg(JExpr._new(dryRunObserverJClass)));
        JBlock dryRunConditionalElseBlock = dryRunConditional._else();
        JVar wsDAOManagerVar = dryRunConditionalElseBlock.decl(soapDAOManagerJClass, "daoMgr");
        wsDAOManagerVar.init(soapDAOManagerJClass.staticInvoke("getInstance"));

        JVar mapseqDAOBeanVar = dryRunConditionalElseBlock.decl(mapseqDAOBeanServiceJClass, "daoBean",
                wsDAOManagerVar.invoke("getMaPSeqDAOBeanService"));
        dryRunConditionalElseBlock.add(moduleExecutorVar.invoke("setDaoBean").arg(mapseqDAOBeanVar));
        dryRunConditionalElseBlock.add(
                moduleExecutorVar.invoke("addObserver").arg(JExpr._new(updateJobObserverJClass).arg(mapseqDAOBeanVar)));

        JVar executorServiceVar = mainMethodBlock.decl(executorServiceJClass, "executorService");
        executorServiceVar.init(executorsJClass.staticInvoke("newSingleThreadExecutor"));

        JVar futureVar = mainMethodBlock.decl(futureJClass.narrow(moduleOutputJClass), "future");
        futureVar.init(executorServiceVar.invoke("submit").arg(moduleExecutorVar));
        mainMethodBlock.add(executorServiceVar.invoke("shutdown"));
        mainMethodBlock.add(executorServiceVar.invoke("awaitTermination")
                .arg(longJClass.staticInvoke("valueOf")
                        .arg(JExpr.lit(clazz.getAnnotation(Application.class).wallTime())))
                .arg(timeUnitJClass.staticRef("DAYS")));
        mainMethodBlock.assign(moduleOutputVar, futureVar.invoke("get"));

        mainMethodBlock._return(moduleOutputVar);
    }

    private void buildMain(Class<?> clazz, JCodeModel codeModel, JDefinedClass cliClass, JFieldVar cliOptionsFieldVar,
            JFieldVar helpFormatterVar) {
        JClass optionBuilderJClass = codeModel.ref(OptionBuilder.class);
        JClass parseExceptionJClass = codeModel.ref(ParseException.class);
        JClass exceptionJClass = codeModel.ref(Exception.class);
        JClass commandLineParserJClass = codeModel.ref(CommandLineParser.class);
        JClass commandLineJClass = codeModel.ref(CommandLine.class);
        JClass gnuParserJClass = codeModel.ref(GnuParser.class);
        JClass moduleOutputJClass = codeModel.ref(ModuleOutput.class);
        JClass stringJClass = codeModel.ref(String.class);
        JClass longJClass = codeModel.ref(Long.class);
        JClass fileJClass = codeModel.ref(File.class);
        JClass dateJClass = codeModel.ref(Date.class);
        JClass booleanJClass = codeModel.ref(Boolean.class);
        JClass listJClass = codeModel.ref(List.class);
        JClass linkedListJClass = codeModel.ref(LinkedList.class);
        JClass systemJClass = codeModel.ref(System.class);
        JClass applicationJClass = codeModel.ref(clazz);
        JClass suppressWarningsJClass = codeModel.ref(SuppressWarnings.class);
        JClass stringUtilsJClass = codeModel.ref(StringUtils.class);

        JMethod mainMethod = cliClass.method(JMod.PUBLIC | JMod.STATIC, void.class, "main");
        JVar paramElement = mainMethod.param(stringJClass.array(), "args");

        JAnnotationUse suppressWarningsAnnotationUse = mainMethod.annotate(suppressWarningsJClass);
        suppressWarningsAnnotationUse.param("value", "static-access");

        JBlock mainMethodBlock = mainMethod.body();

        Field[] fieldArray = clazz.getDeclaredFields();

        mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption")
                .arg(optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit("workflowRunAttemptId"))
                        .invoke("hasArg").invoke("withDescription").arg(JExpr.lit("WorkflowRunAttempt identifier"))
                        .invoke("withLongOpt").arg(JExpr.lit("workflowRunAttemptId")).invoke("create")));

        mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption")
                .arg(optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit("sampleId")).invoke("hasArg")
                        .invoke("withDescription").arg(JExpr.lit("Sample identifier")).invoke("withLongOpt")
                        .arg(JExpr.lit("sampleId")).invoke("create")));

        mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption")
                .arg(optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit("dryRun")).invoke("withDescription")
                        .arg(JExpr.lit("no web service calls & echo command line without running"))
                        .invoke("withLongOpt").arg(JExpr.lit("dryRun")).invoke("create")));

        mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption")
                .arg(optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit("persistFileData"))
                        .invoke("withDescription").arg(JExpr.lit("persist FileData's if they exist"))
                        .invoke("withLongOpt").arg(JExpr.lit("persistFileData")).invoke("create")));

        mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption")
                .arg(optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit("serializeFile")).invoke("hasArg")
                        .invoke("withDescription").arg(JExpr.lit("Serialize File")).invoke("withLongOpt")
                        .arg(JExpr.lit("serializeFile")).invoke("create")));

        for (Field field : fieldArray) {
            if (field.isAnnotationPresent(InputArgument.class)) {
                InputArgument input = field.getAnnotation(InputArgument.class);

                if (field.getType() == Boolean.class) {

                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption")
                            .arg(optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("withDescription").arg(JExpr.lit(input.description())).invoke("withLongOpt")
                                    .arg(JExpr.lit(field.getName())).invoke("create")));

                } else if (field.getType().isEnum()) {

                    String description = String.format("%s...%s", input.description(),
                            Arrays.asList(field.getType().getEnumConstants()));
                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption")
                            .arg(optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("hasArgs").invoke("withDescription").arg(JExpr.lit(description))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                } else if (field.getType() == List.class) {

                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption")
                            .arg(optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("hasArgs").invoke("withDescription").arg(JExpr.lit(input.description()))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                } else if (field.getType() == Date.class) {

                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption")
                            .arg(optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("hasArgs").invoke("withDescription").arg(JExpr.lit(""))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                } else {

                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption")
                            .arg(optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("hasArgs").invoke("withDescription").arg(JExpr.lit(input.description()))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                }

            }

            if (field.isAnnotationPresent(OutputArgument.class)) {
                OutputArgument output = field.getAnnotation(OutputArgument.class);

                if (field.getType() == Boolean.class) {

                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption")
                            .arg(optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("withDescription").arg(JExpr.lit(output.description()))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                } else if (field.getType().isEnum()) {

                    String description = String.format("%s...%s", output.description(),
                            Arrays.asList(field.getType().getEnumConstants()));
                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption")
                            .arg(optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("hasArgs").invoke("withDescription").arg(JExpr.lit(description))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                } else if (field.getType() == List.class) {

                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption")
                            .arg(optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("hasArgs").invoke("withDescription").arg(JExpr.lit(output.description()))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                } else if (field.getType() == Date.class) {

                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption")
                            .arg(optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("hasArgs").invoke("withDescription").arg(JExpr.lit(""))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                } else {

                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption")
                            .arg(optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("hasArgs").invoke("withDescription").arg(JExpr.lit(output.description()))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                }

            }

        }

        mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption")
                .arg(optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit("help")).invoke("withDescription")
                        .arg(JExpr.lit("print this help message")).invoke("withLongOpt").arg("help").invoke("create")
                        .arg(JExpr.lit("?"))));

        JVar applicationVar = mainMethodBlock.decl(applicationJClass, "app");
        applicationVar.init(JExpr._new(applicationJClass));

        JTryBlock tryBlock = mainMethodBlock._try();
        JBlock tryBlockBody = tryBlock.body();

        JVar clpVar = tryBlockBody.decl(commandLineParserJClass, "commandLineParser");
        clpVar.init(JExpr._new(gnuParserJClass));

        JVar commandLineVar = tryBlockBody.decl(commandLineJClass, "commandLine");
        commandLineVar.init(clpVar.invoke("parse").arg(cliOptionsFieldVar).arg(paramElement));

        JConditional conditional = tryBlockBody._if(commandLineVar.invoke("hasOption").arg("?"));
        JBlock conditionalThenBlock = conditional._then();
        conditionalThenBlock
                .add(helpFormatterVar.invoke("printHelp").arg(clazz.getSimpleName() + "CLI").arg(cliOptionsFieldVar));
        conditionalThenBlock._return();

        String paramName = "workflowRunAttemptId";
        JConditional hasOptionConditional = tryBlockBody._if(commandLineVar.invoke("hasOption").arg(paramName));
        JBlock hasOptionConditionalThenBlock = hasOptionConditional._then();
        JVar paramVar = hasOptionConditionalThenBlock.decl(longJClass, paramName);
        paramVar.init(longJClass.staticInvoke("valueOf").arg(commandLineVar.invoke("getOptionValue").arg(paramName)));
        hasOptionConditionalThenBlock
                .add(applicationVar.invoke(String.format("set%s", StringUtils.capitalize(paramName))).arg(paramVar));

        paramName = "sampleId";
        hasOptionConditional = tryBlockBody._if(commandLineVar.invoke("hasOption").arg(paramName));
        hasOptionConditionalThenBlock = hasOptionConditional._then();
        paramVar = hasOptionConditionalThenBlock.decl(longJClass, paramName);
        paramVar.init(longJClass.staticInvoke("valueOf").arg(commandLineVar.invoke("getOptionValue").arg(paramName)));
        hasOptionConditionalThenBlock
                .add(applicationVar.invoke(String.format("set%s", StringUtils.capitalize(paramName))).arg(paramVar));

        paramName = "serializeFile";
        hasOptionConditional = tryBlockBody._if(commandLineVar.invoke("hasOption").arg(paramName));
        hasOptionConditionalThenBlock = hasOptionConditional._then();
        paramVar = hasOptionConditionalThenBlock.decl(fileJClass, paramName);
        paramVar.init(JExpr._new(fileJClass).arg(commandLineVar.invoke("getOptionValue").arg(paramName)));
        hasOptionConditionalThenBlock
                .add(applicationVar.invoke(String.format("set%s", StringUtils.capitalize(paramName))).arg(paramVar));

        hasOptionConditional = tryBlockBody._if(commandLineVar.invoke("hasOption").arg("dryRun"));
        hasOptionConditionalThenBlock = hasOptionConditional._then();
        hasOptionConditionalThenBlock.add(
                applicationVar.invoke("set" + StringUtils.capitalize("dryRun")).arg(booleanJClass.staticRef("TRUE")));

        hasOptionConditional = tryBlockBody._if(commandLineVar.invoke("hasOption").arg("persistFileData"));
        hasOptionConditionalThenBlock = hasOptionConditional._then();
        hasOptionConditionalThenBlock.add(applicationVar.invoke("set" + StringUtils.capitalize("persistFileData"))
                .arg(booleanJClass.staticRef("TRUE")));

        for (Field field : fieldArray) {
            if (field.isAnnotationPresent(InputArgument.class) || field.isAnnotationPresent(OutputArgument.class)) {
                // Input input = field.getAnnotation(Input.class);

                String capitalizedFieldName = StringUtils.capitalize(field.getName());
                hasOptionConditional = tryBlockBody._if(commandLineVar.invoke("hasOption").arg(field.getName()));
                hasOptionConditionalThenBlock = hasOptionConditional._then();

                JClass typeClass = codeModel.ref(field.getType());
                if (field.getType() == File.class) {
                    paramVar = hasOptionConditionalThenBlock.decl(typeClass, field.getName());
                    paramVar.init(
                            JExpr._new(typeClass).arg(commandLineVar.invoke("getOptionValue").arg(field.getName())));
                    hasOptionConditionalThenBlock
                            .add(applicationVar.invoke(String.format("set%s", capitalizedFieldName)).arg(paramVar));
                } else if (field.getType() == String.class) {
                    paramVar = hasOptionConditionalThenBlock.decl(typeClass.array(), field.getName());
                    paramVar.init(commandLineVar.invoke("getOptionValues").arg(field.getName()));
                    hasOptionConditionalThenBlock
                            .add(applicationVar.invoke(String.format("set%s", capitalizedFieldName))
                                    .arg(stringUtilsJClass.staticInvoke("join").arg(paramVar).arg(JExpr.lit(" "))));
                } else if (field.getType() == Date.class) {
                    paramVar = hasOptionConditionalThenBlock.decl(typeClass, field.getName());
                    paramVar.init(JExpr._new(dateJClass).arg(longJClass.staticInvoke("valueOf")
                            .arg(commandLineVar.invoke("getOptionValue").arg(field.getName()))));
                    hasOptionConditionalThenBlock
                            .add(applicationVar.invoke(String.format("set%s", capitalizedFieldName)).arg(paramVar));
                } else if (field.getType().isEnum()) {

                    JTryBlock enumCastTryBlock = hasOptionConditionalThenBlock._try();
                    JBlock enumCastTryBlockBody = enumCastTryBlock.body();

                    paramVar = enumCastTryBlockBody.decl(typeClass, field.getName());
                    paramVar.init(typeClass.staticInvoke("valueOf")
                            .arg(commandLineVar.invoke("getOptionValue").arg(field.getName())));
                    enumCastTryBlockBody
                            .add(applicationVar.invoke(String.format("set%s", capitalizedFieldName)).arg(paramVar));

                    JCatchBlock catchBlock = enumCastTryBlock._catch(exceptionJClass);
                    JVar exceptionVar = catchBlock.param("e");
                    JBlock catchBlockBody = catchBlock.body();
                    JClass typeJClass = codeModel.ref(field.getType());
                    JClass arraysJClass = codeModel.ref(Arrays.class);

                    catchBlockBody.add(systemJClass.staticRef("err").invoke("format")
                            .arg(JExpr.lit("Enum name:  %s%nEnum constants:  %s%n"))
                            .arg(typeJClass.staticRef("class").invoke("getName"))
                            .arg(arraysJClass.staticInvoke("asList")
                                    .arg(typeJClass.staticRef("class").invoke("getEnumConstants"))));

                    catchBlockBody.add(systemJClass.staticRef("err").invoke("println")
                            .arg(JExpr.lit("Parsing Failed: ").plus(exceptionVar.invoke("getMessage"))));
                    catchBlockBody.add(helpFormatterVar.invoke("printHelp").arg(clazz.getSimpleName() + "CLI")
                            .arg(cliOptionsFieldVar));
                    catchBlockBody._return();

                } else if (field.getType() == List.class) {
                    ParameterizedType listType = (ParameterizedType) field.getGenericType();
                    Class<?> listTypeClass = (Class<?>) listType.getActualTypeArguments()[0];
                    JClass typeJClass = codeModel.ref(listTypeClass);
                    paramVar = hasOptionConditionalThenBlock.decl(listJClass.narrow(typeJClass),
                            field.getName() + "List");
                    paramVar.init(JExpr._new(linkedListJClass.narrow(listTypeClass)));

                    JForEach paramForEach = hasOptionConditionalThenBlock.forEach(stringJClass, "a",
                            commandLineVar.invoke("getOptionValues").arg(field.getName()));
                    JBlock paramForEachBody = paramForEach.body();

                    paramForEachBody.add(paramVar.invoke("add").arg(JExpr._new(typeJClass).arg(paramForEach.var())));
                    hasOptionConditionalThenBlock
                            .add(applicationVar.invoke(String.format("set%s", capitalizedFieldName)).arg(paramVar));

                } else if (field.getType() == Boolean.class) {

                    hasOptionConditionalThenBlock.add(applicationVar
                            .invoke(String.format("set%s", capitalizedFieldName)).arg(booleanJClass.staticRef("TRUE")));

                } else {
                    paramVar = hasOptionConditionalThenBlock.decl(typeClass, field.getName());
                    paramVar.init(typeClass.staticInvoke("valueOf")
                            .arg(commandLineVar.invoke("getOptionValue").arg(field.getName())));
                    hasOptionConditionalThenBlock
                            .add(applicationVar.invoke(String.format("set%s", capitalizedFieldName)).arg(paramVar));
                }

            }
        }

        JCatchBlock catchBlock = tryBlock._catch(parseExceptionJClass);
        JVar exceptionVar = catchBlock.param("e");
        JBlock catchBlockBody = catchBlock.body();
        catchBlockBody.add(systemJClass.staticRef("err").invoke("println")
                .arg(JExpr.lit("Parsing Failed: ").plus(exceptionVar.invoke("getMessage"))));
        catchBlockBody
                .add(helpFormatterVar.invoke("printHelp").arg(clazz.getSimpleName() + "CLI").arg(cliOptionsFieldVar));

        catchBlockBody.add(systemJClass.staticInvoke("exit").arg(JExpr.lit(-1)));

        JVar moduleOutputVar = mainMethodBlock.decl(moduleOutputJClass, "moduleOutput");
        moduleOutputVar.init(JExpr._null());
        tryBlock = mainMethodBlock._try();
        tryBlockBody = tryBlock.body();

        JVar appCLIVar = tryBlockBody.decl(cliClass, "appCLI");
        appCLIVar.init(JExpr._new(cliClass).arg(applicationVar));
        tryBlockBody.assign(moduleOutputVar, appCLIVar.invoke("call"));

        catchBlock = tryBlock._catch(exceptionJClass);
        exceptionVar = catchBlock.param("e");
        catchBlockBody = catchBlock.body();
        catchBlockBody.add(exceptionVar.invoke("printStackTrace"));
        catchBlockBody
                .add(helpFormatterVar.invoke("printHelp").arg(clazz.getSimpleName() + "CLI").arg(cliOptionsFieldVar));
        catchBlockBody.add(systemJClass.staticInvoke("exit").arg(JExpr.lit(-1)));

        JConditional nullModuleOutputConditional = mainMethodBlock._if(moduleOutputVar.eq(JExpr._null()));
        JBlock nullModuleOutputConditionalThenBlock = nullModuleOutputConditional._then();
        nullModuleOutputConditionalThenBlock
                .add(systemJClass.staticRef("err").invoke("println").arg(JExpr.lit("moduleOutput is null")));
        nullModuleOutputConditionalThenBlock
                .add(helpFormatterVar.invoke("printHelp").arg(clazz.getSimpleName() + "CLI").arg(cliOptionsFieldVar));
        nullModuleOutputConditionalThenBlock.add(systemJClass.staticInvoke("exit").arg(JExpr.lit(-1)));

        mainMethodBlock.add(systemJClass.staticInvoke("exit").arg(moduleOutputVar.invoke("getExitCode")));

    }

    /*
     * intended for testing...not real world use
     */
    public static void main(String[] args) {
        ReflectionManager reflectionManager = ReflectionManager.getInstance();

        List<Class<?>> filteredClassList = new ArrayList<Class<?>>();
        List<Class<?>> classList = new ArrayList<Class<?>>();

        String pkg = "edu.unc.mapseq.module.impl";
        classList.addAll(reflectionManager.lookupClassList(pkg, null, Application.class));

        for (Class<?> c : classList) {
            if (!c.isAnnotationPresent(Ignore.class)) {
                filteredClassList.add(c);
            }
        }

        File f = new File("/tmp/java");
        f.mkdirs();
        Runnable generator = new ModuleCLIGenerator(filteredClassList, pkg, f.getAbsolutePath());
        generator.run();
    }

}

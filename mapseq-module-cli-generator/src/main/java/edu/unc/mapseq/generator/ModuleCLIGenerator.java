package edu.unc.mapseq.generator;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

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

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.ws.WSDAOManager;
import edu.unc.mapseq.module.DryRunJobObserver;
import edu.unc.mapseq.module.ModuleExecutor;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.PersistantJobObserver;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.Ignore;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;

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
                String newClass = clazz.getPackage().toString().replace("package ", "") + "." + clazz.getSimpleName()
                        + "CLI";
                System.out.println("Generating new class: " + newClass);
                JDefinedClass cliClass = codeModel._class(newClass);

                JClass runnableJClass = codeModel.ref(Runnable.class);
                JClass helpFormatterJClass = codeModel.ref(HelpFormatter.class);
                JClass applicationJClass = codeModel.ref(clazz);
                JClass optionsJClass = codeModel.ref(Options.class);
                JClass stringJClass = codeModel.ref(String.class);

                cliClass._implements(runnableJClass);

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

        JClass mapseqDAOBeanJClass = codeModel.ref(MaPSeqDAOBean.class);
        JClass wsDAOManagerJClass = codeModel.ref(WSDAOManager.class);
        JClass moduleExecutorJClass = codeModel.ref(ModuleExecutor.class);
        JClass moduleOutputJClass = codeModel.ref(ModuleOutput.class);
        JClass executorsJClass = codeModel.ref(Executors.class);
        JClass executorServiceJClass = codeModel.ref(ExecutorService.class);
        JClass futureJClass = codeModel.ref(Future.class);
        JClass exceptionJClass = codeModel.ref(Exception.class);
        JClass systemJClass = codeModel.ref(System.class);
        JClass applicationJClass = codeModel.ref(clazz);
        JClass validatorJClass = codeModel.ref(Validator.class);
        JClass setJClass = codeModel.ref(Set.class);
        JClass constraintViolationJClass = codeModel.ref(ConstraintViolation.class);
        JClass outputChecksJClass = codeModel.ref(OutputValidations.class);
        JClass inputChecksJClass = codeModel.ref(InputValidations.class);
        JClass messageFormatJClass = codeModel.ref(MessageFormat.class);
        JClass stringJClass = codeModel.ref(String.class);
        JClass validatorFactoryJClass = codeModel.ref(ValidatorFactory.class);
        JClass validationJClass = codeModel.ref(Validation.class);
        JClass dryRunObserverJClass = codeModel.ref(DryRunJobObserver.class);
        JClass updateJobObserverJClass = codeModel.ref(PersistantJobObserver.class);

        JMethod mainMethod = cliClass.method(JMod.PUBLIC, void.class, "run");

        JBlock mainMethodBlock = mainMethod.body();

        JVar validatorFactoryVar = mainMethodBlock.decl(validatorFactoryJClass, "validatorFactory");
        validatorFactoryVar.init(validationJClass.staticInvoke("buildDefaultValidatorFactory"));

        JVar validatorVar = mainMethodBlock.decl(validatorJClass, "validator");
        validatorVar.init(validatorFactoryVar.invoke("getValidator"));

        JVar moduleOutputVar = mainMethodBlock.decl(moduleOutputJClass, "output");
        moduleOutputVar.init(JExpr._null());

        JTryBlock tryBlock = mainMethodBlock._try();
        JBlock tryBlockBody = tryBlock.body();

        JVar constraintViolationsVar = tryBlockBody.decl(
                setJClass.narrow(constraintViolationJClass.narrow(applicationJClass)), "constraintViolations");
        constraintViolationsVar
                .init(validatorVar.invoke("validate").arg(appFieldVar).arg(inputChecksJClass.dotclass()));

        JConditional constraintViolationsVarSizeConditional = tryBlockBody._if(constraintViolationsVar.invoke("size")
                .gt(JExpr.lit(0)).cand(appFieldVar.invoke("getDryRun").not()));
        JBlock constraintViolationsVarSizeConditionalThenBlock = constraintViolationsVarSizeConditional._then();

        JForEach paramForEach = constraintViolationsVarSizeConditionalThenBlock.forEach(
                constraintViolationJClass.narrow(applicationJClass), "value", constraintViolationsVar);
        JBlock paramForEachBody = paramForEach.body();

        JVar errorMessageVar = paramForEachBody.decl(stringJClass, "errorMessage");
        errorMessageVar.init(messageFormatJClass.staticInvoke("format").arg("The value of {0}.{1} was: {2}.  {3}")
                .arg(paramForEach.var().invoke("getRootBeanClass"))
                .arg(paramForEach.var().invoke("getPropertyPath").invoke("toString"))
                .arg(paramForEach.var().invoke("getInvalidValue")).arg(paramForEach.var().invoke("getMessage")));

        paramForEachBody.add(systemJClass.staticRef("err").invoke("println").arg(errorMessageVar));

        constraintViolationsVarSizeConditionalThenBlock.add(helpFormatterVar.invoke("printHelp")
                .arg(clazz.getSimpleName() + "CLI").arg(cliOptionsFieldVar));
        constraintViolationsVarSizeConditionalThenBlock.add(systemJClass.staticInvoke("exit").arg(JExpr.lit(-1)));

        JVar moduleExecutorVar = tryBlockBody.decl(moduleExecutorJClass, "executor");
        moduleExecutorVar.init(JExpr._new(moduleExecutorJClass));

        tryBlockBody.add(moduleExecutorVar.invoke("setModule").arg(appFieldVar));

        JConditional dryRunConditional = tryBlockBody._if(appFieldVar.invoke("getDryRun"));
        JBlock dryRunConditionalBlock = dryRunConditional._then();
        dryRunConditionalBlock.add(moduleExecutorVar.invoke("addObserver").arg(JExpr._new(dryRunObserverJClass)));
        JBlock dryRunConditionalElseBlock = dryRunConditional._else();
        JVar wsDAOManagerVar = dryRunConditionalElseBlock.decl(wsDAOManagerJClass, "daoMgr");
        wsDAOManagerVar.init(wsDAOManagerJClass.staticInvoke("getInstance"));

        JVar mapseqDAOBeanVar = dryRunConditionalElseBlock.decl(mapseqDAOBeanJClass, "daoBean",
                wsDAOManagerVar.invoke("getMaPSeqDAOBean"));
        dryRunConditionalElseBlock.add(moduleExecutorVar.invoke("setDaoBean").arg(mapseqDAOBeanVar));
        dryRunConditionalElseBlock.add(moduleExecutorVar.invoke("addObserver").arg(
                JExpr._new(updateJobObserverJClass).arg(mapseqDAOBeanVar)));

        JVar executorServiceVar = tryBlockBody.decl(executorServiceJClass, "executorService");
        executorServiceVar.init(executorsJClass.staticInvoke("newSingleThreadExecutor"));

        JVar futureVar = tryBlockBody.decl(futureJClass.narrow(moduleOutputJClass), "future");
        futureVar.init(executorServiceVar.invoke("submit").arg(moduleExecutorVar));
        tryBlockBody.assign(moduleOutputVar, futureVar.invoke("get"));
        tryBlockBody.add(executorServiceVar.invoke("shutdown"));

        tryBlockBody.assign(constraintViolationsVar,
                validatorVar.invoke("validate").arg(appFieldVar).arg(outputChecksJClass.dotclass()));

        constraintViolationsVarSizeConditional = tryBlockBody._if(constraintViolationsVar.invoke("size").gt(
                JExpr.lit(0)).cand(appFieldVar.invoke("getDryRun").not()));
        constraintViolationsVarSizeConditionalThenBlock = constraintViolationsVarSizeConditional._then();

        paramForEach = constraintViolationsVarSizeConditionalThenBlock.forEach(
                constraintViolationJClass.narrow(applicationJClass), "value", constraintViolationsVar);
        paramForEachBody = paramForEach.body();

        errorMessageVar = paramForEachBody.decl(stringJClass, "errorMessage");
        errorMessageVar.init(messageFormatJClass.staticInvoke("format").arg("The value of {0}.{1} was: {2}.  {3}")
                .arg(paramForEach.var().invoke("getRootBeanClass"))
                .arg(paramForEach.var().invoke("getPropertyPath").invoke("toString"))
                .arg(paramForEach.var().invoke("getInvalidValue")).arg(paramForEach.var().invoke("getMessage")));

        paramForEachBody.add(systemJClass.staticRef("err").invoke("println").arg(errorMessageVar));

        constraintViolationsVarSizeConditionalThenBlock.add(systemJClass.staticInvoke("exit").arg(JExpr.lit(-1)));

        JCatchBlock catchBlock = tryBlock._catch(exceptionJClass);

        JVar exceptionVar = catchBlock.param("e");
        JBlock catchBlockBody = catchBlock.body();
        catchBlockBody.add(exceptionVar.invoke("printStackTrace"));
        JBlock finallyBlock = tryBlock._finally();

        JConditional outputNullCheckConditional = finallyBlock._if(moduleOutputVar.eq(JExpr._null()));
        JBlock outputNullCheckConditionalThenBlock = outputNullCheckConditional._then();
        outputNullCheckConditionalThenBlock.add(systemJClass.staticInvoke("exit").arg(JExpr.lit(-1)));

        finallyBlock.add(systemJClass.staticInvoke("exit").arg(moduleOutputVar.invoke("getExitCode")));

    }

    private void buildMain(Class<?> clazz, JCodeModel codeModel, JDefinedClass cliClass, JFieldVar cliOptionsFieldVar,
            JFieldVar helpFormatterVar) {
        JClass optionBuilderJClass = codeModel.ref(OptionBuilder.class);
        JClass parseExceptionJClass = codeModel.ref(ParseException.class);
        JClass exceptionJClass = codeModel.ref(Exception.class);
        JClass commandLineParserJClass = codeModel.ref(CommandLineParser.class);
        JClass commandLineJClass = codeModel.ref(CommandLine.class);
        JClass gnuParserJClass = codeModel.ref(GnuParser.class);
        JClass stringJClass = codeModel.ref(String.class);
        JClass longJClass = codeModel.ref(Long.class);
        JClass propertiesJClass = codeModel.ref(Properties.class);
        JClass fileJClass = codeModel.ref(File.class);
        JClass fileInputStreamJClass = codeModel.ref(FileInputStream.class);
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

        mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit("sequencerRunId")).invoke("hasArg")
                        .invoke("withDescription").arg(JExpr.lit("SequencerRun identifier")).invoke("withLongOpt")
                        .arg(JExpr.lit("sequencerRunId")).invoke("create")));

        mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit("workflowRunId")).invoke("hasArg")
                        .invoke("withDescription").arg(JExpr.lit("WorkflowRun identifier")).invoke("withLongOpt")
                        .arg(JExpr.lit("workflowRunId")).invoke("create")));

        mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit("htsfSampleId")).invoke("hasArg")
                        .invoke("withDescription").arg(JExpr.lit("HTSF Sample Identifier")).invoke("withLongOpt")
                        .arg(JExpr.lit("htsfSampleId")).invoke("create")));

        mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit("accountId")).invoke("hasArg")
                        .invoke("withDescription").arg(JExpr.lit("Account Identifier")).invoke("withLongOpt")
                        .arg(JExpr.lit("accountId")).invoke("create")));

        mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit("propertyFile")).invoke("hasArg")
                        .invoke("withDescription").arg(JExpr.lit("Property File")).invoke("withLongOpt")
                        .arg(JExpr.lit("propertyFile")).invoke("create")));

        mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit("dryRun")).invoke("withDescription")
                        .arg(JExpr.lit("no web service calls & echo command line without running"))
                        .invoke("withLongOpt").arg(JExpr.lit("dryRun")).invoke("create")));

        mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit("persistFileData"))
                        .invoke("withDescription").arg(JExpr.lit("persist FileData's if they exist"))
                        .invoke("withLongOpt").arg(JExpr.lit("persistFileData")).invoke("create")));

        mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit("parentJob")).invoke("hasArg")
                        .invoke("withDescription").arg(JExpr.lit("Serialized Parent Job file")).invoke("withLongOpt")
                        .arg(JExpr.lit("parentJob")).invoke("create")));

        mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit("serializeFile")).invoke("hasArg")
                        .invoke("withDescription").arg(JExpr.lit("Serialize File")).invoke("withLongOpt")
                        .arg(JExpr.lit("serializeFile")).invoke("create")));

        for (Field field : fieldArray) {
            if (field.isAnnotationPresent(InputArgument.class)) {
                InputArgument input = field.getAnnotation(InputArgument.class);

                if (field.getType() == Boolean.class) {

                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                            optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("withDescription").arg(JExpr.lit(input.description()))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                } else if (field.getType().isEnum()) {

                    String description = String.format("%s...%s", input.description(),
                            Arrays.asList(field.getType().getEnumConstants()));
                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                            optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("hasArgs").invoke("withDescription").arg(JExpr.lit(description))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                } else if (field.getType() == List.class) {

                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                            optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("hasArgs").invoke("withDescription").arg(JExpr.lit(input.description()))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                } else if (field.getType() == Date.class) {

                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                            optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("hasArgs").invoke("withDescription").arg(JExpr.lit(""))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                } else {

                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                            optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("hasArgs").invoke("withDescription").arg(JExpr.lit(input.description()))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                }

            }

            if (field.isAnnotationPresent(OutputArgument.class)) {
                OutputArgument output = field.getAnnotation(OutputArgument.class);

                if (field.getType() == Boolean.class) {

                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                            optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("withDescription").arg(JExpr.lit(output.description()))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                } else if (field.getType().isEnum()) {

                    String description = String.format("%s...%s", output.description(),
                            Arrays.asList(field.getType().getEnumConstants()));
                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                            optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("hasArgs").invoke("withDescription").arg(JExpr.lit(description))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                } else if (field.getType() == List.class) {

                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                            optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("hasArgs").invoke("withDescription").arg(JExpr.lit(output.description()))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                } else if (field.getType() == Date.class) {

                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                            optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("hasArgs").invoke("withDescription").arg(JExpr.lit(""))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                } else {

                    mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                            optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit(field.getName()))
                                    .invoke("hasArgs").invoke("withDescription").arg(JExpr.lit(output.description()))
                                    .invoke("withLongOpt").arg(JExpr.lit(field.getName())).invoke("create")));

                }

            }

        }

        mainMethodBlock.add(cliOptionsFieldVar.invoke("addOption").arg(
                optionBuilderJClass.staticInvoke("withArgName").arg(JExpr.lit("help")).invoke("withDescription")
                        .arg(JExpr.lit("print this help message")).invoke("withLongOpt").arg("help").invoke("create")
                        .arg(JExpr.lit("?"))));

        JVar clpVar = mainMethodBlock.decl(commandLineParserJClass, "commandLineParser");
        clpVar.init(JExpr._new(gnuParserJClass));

        JVar applicationVar = mainMethodBlock.decl(applicationJClass, "app");
        applicationVar.init(JExpr._new(applicationJClass));

        JTryBlock tryBlock = mainMethodBlock._try();
        JBlock tryBlockBody = tryBlock.body();

        JVar commandLineVar = tryBlockBody.decl(commandLineJClass, "commandLine");
        commandLineVar.init(clpVar.invoke("parse").arg(cliOptionsFieldVar).arg(paramElement));

        JConditional conditional = tryBlockBody._if(commandLineVar.invoke("hasOption").arg("?"));
        JBlock conditionalThenBlock = conditional._then();
        conditionalThenBlock.add(helpFormatterVar.invoke("printHelp").arg(clazz.getSimpleName() + "CLI")
                .arg(cliOptionsFieldVar));
        conditionalThenBlock._return();

        JConditional hasOptionConditional = tryBlockBody._if(commandLineVar.invoke("hasOption").arg("workflowRunId"));
        JBlock hasOptionConditionalThenBlock = hasOptionConditional._then();

        JVar paramVar = hasOptionConditionalThenBlock.decl(longJClass, "workflowRunId");
        paramVar.init(longJClass.staticInvoke("valueOf").arg(
                commandLineVar.invoke("getOptionValue").arg("workflowRunId")));
        hasOptionConditionalThenBlock.add(applicationVar.invoke("set" + StringUtils.capitalize("workflowRunId")).arg(
                paramVar));

        hasOptionConditional = tryBlockBody._if(commandLineVar.invoke("hasOption").arg("sequencerRunId"));
        hasOptionConditionalThenBlock = hasOptionConditional._then();
        paramVar = hasOptionConditionalThenBlock.decl(longJClass, "sequencerRunId");
        paramVar.init(longJClass.staticInvoke("valueOf").arg(
                commandLineVar.invoke("getOptionValue").arg("sequencerRunId")));
        hasOptionConditionalThenBlock.add(applicationVar.invoke("set" + StringUtils.capitalize("sequencerRunId")).arg(
                paramVar));

        hasOptionConditional = tryBlockBody._if(commandLineVar.invoke("hasOption").arg("htsfSampleId"));
        hasOptionConditionalThenBlock = hasOptionConditional._then();
        paramVar = hasOptionConditionalThenBlock.decl(longJClass, "htsfSampleId");
        paramVar.init(longJClass.staticInvoke("valueOf").arg(
                commandLineVar.invoke("getOptionValue").arg("htsfSampleId")));
        hasOptionConditionalThenBlock.add(applicationVar.invoke("setHTSFSampleId").arg(paramVar));

        hasOptionConditional = tryBlockBody._if(commandLineVar.invoke("hasOption").arg("accountId"));
        hasOptionConditionalThenBlock = hasOptionConditional._then();
        paramVar = hasOptionConditionalThenBlock.decl(longJClass, "accountId");
        paramVar.init(longJClass.staticInvoke("valueOf").arg(commandLineVar.invoke("getOptionValue").arg("accountId")));
        hasOptionConditionalThenBlock.add(applicationVar.invoke("setAccountId").arg(paramVar));

        hasOptionConditional = tryBlockBody._if(commandLineVar.invoke("hasOption").arg("serializeFile"));
        hasOptionConditionalThenBlock = hasOptionConditional._then();
        paramVar = hasOptionConditionalThenBlock.decl(fileJClass, "serializeFile");
        paramVar.init(JExpr._new(fileJClass).arg(commandLineVar.invoke("getOptionValue").arg("serializeFile")));
        hasOptionConditionalThenBlock.add(applicationVar.invoke("setSerializeFile").arg(paramVar));

        hasOptionConditional = tryBlockBody._if(commandLineVar.invoke("hasOption").arg("propertyFile"));
        hasOptionConditionalThenBlock = hasOptionConditional._then();
        paramVar = hasOptionConditionalThenBlock.decl(fileJClass, "propertyFile");
        paramVar.init(JExpr._new(fileJClass).arg(commandLineVar.invoke("getOptionValue").arg("propertyFile")));
        JVar propertiesVar = hasOptionConditionalThenBlock.decl(propertiesJClass, "properties");
        propertiesVar.init(JExpr._new(propertiesJClass));
        hasOptionConditionalThenBlock.add(propertiesVar.invoke("load").arg(
                JExpr._new(fileInputStreamJClass).arg(paramVar)));
        hasOptionConditionalThenBlock.add(applicationVar.invoke("setProperties").arg(propertiesVar));

        hasOptionConditional = tryBlockBody._if(commandLineVar.invoke("hasOption").arg("dryRun"));
        hasOptionConditionalThenBlock = hasOptionConditional._then();
        hasOptionConditionalThenBlock.add(applicationVar.invoke("set" + StringUtils.capitalize("dryRun")).arg(
                booleanJClass.staticRef("TRUE")));

        hasOptionConditional = tryBlockBody._if(commandLineVar.invoke("hasOption").arg("persistFileData"));
        hasOptionConditionalThenBlock = hasOptionConditional._then();
        hasOptionConditionalThenBlock.add(applicationVar.invoke("set" + StringUtils.capitalize("persistFileData")).arg(
                booleanJClass.staticRef("TRUE")));

        for (Field field : fieldArray) {
            if (field.isAnnotationPresent(InputArgument.class) || field.isAnnotationPresent(OutputArgument.class)) {
                // Input input = field.getAnnotation(Input.class);

                hasOptionConditional = tryBlockBody._if(commandLineVar.invoke("hasOption").arg(field.getName()));
                hasOptionConditionalThenBlock = hasOptionConditional._then();

                JClass typeClass = codeModel.ref(field.getType());
                if (field.getType() == File.class) {
                    paramVar = hasOptionConditionalThenBlock.decl(typeClass, field.getName());
                    paramVar.init(JExpr._new(typeClass).arg(
                            commandLineVar.invoke("getOptionValue").arg(field.getName())));
                    hasOptionConditionalThenBlock.add(applicationVar.invoke(
                            "set" + StringUtils.capitalize(field.getName())).arg(paramVar));
                } else if (field.getType() == String.class) {
                    paramVar = hasOptionConditionalThenBlock.decl(typeClass.array(), field.getName());
                    paramVar.init(commandLineVar.invoke("getOptionValues").arg(field.getName()));
                    hasOptionConditionalThenBlock.add(applicationVar.invoke(
                            "set" + StringUtils.capitalize(field.getName())).arg(
                            stringUtilsJClass.staticInvoke("join").arg(paramVar).arg(JExpr.lit(" "))));
                } else if (field.getType() == Date.class) {
                    paramVar = hasOptionConditionalThenBlock.decl(typeClass, field.getName());
                    paramVar.init(JExpr._new(dateJClass).arg(
                            longJClass.staticInvoke("valueOf").arg(
                                    commandLineVar.invoke("getOptionValue").arg(field.getName()))));
                    hasOptionConditionalThenBlock.add(applicationVar.invoke(
                            "set" + StringUtils.capitalize(field.getName())).arg(paramVar));
                } else if (field.getType().isEnum()) {

                    JTryBlock enumCastTryBlock = hasOptionConditionalThenBlock._try();
                    JBlock enumCastTryBlockBody = enumCastTryBlock.body();

                    paramVar = enumCastTryBlockBody.decl(typeClass, field.getName());
                    paramVar.init(typeClass.staticInvoke("valueOf").arg(
                            commandLineVar.invoke("getOptionValue").arg(field.getName())));
                    enumCastTryBlockBody.add(applicationVar.invoke("set" + StringUtils.capitalize(field.getName()))
                            .arg(paramVar));

                    JCatchBlock catchBlock = enumCastTryBlock._catch(exceptionJClass);
                    JVar exceptionVar = catchBlock.param("e");
                    JBlock catchBlockBody = catchBlock.body();
                    JClass typeJClass = codeModel.ref(field.getType());
                    JClass arraysJClass = codeModel.ref(Arrays.class);

                    catchBlockBody.add(systemJClass
                            .staticRef("err")
                            .invoke("format")
                            .arg(JExpr.lit("Enum name:  %s%nEnum constants:  %s%n"))
                            .arg(typeJClass.staticRef("class").invoke("getName"))
                            .arg(arraysJClass.staticInvoke("asList").arg(
                                    typeJClass.staticRef("class").invoke("getEnumConstants"))));

                    catchBlockBody.add(systemJClass.staticRef("err").invoke("println")
                            .arg(JExpr.lit("Parsing Failed: ").plus(exceptionVar.invoke("getMessage"))));
                    catchBlockBody.add(helpFormatterVar.invoke("printHelp").arg(clazz.getSimpleName() + "CLI")
                            .arg(cliOptionsFieldVar));
                    catchBlockBody._return();

                } else if (field.getType() == List.class) {
                    ParameterizedType listType = (ParameterizedType) field.getGenericType();
                    Class<?> listTypeClass = (Class<?>) listType.getActualTypeArguments()[0];
                    JClass typeJClass = codeModel.ref(listTypeClass);
                    paramVar = hasOptionConditionalThenBlock.decl(listJClass.narrow(typeJClass), field.getName()
                            + "List");
                    paramVar.init(JExpr._new(linkedListJClass.narrow(listTypeClass)));

                    JForEach paramForEach = hasOptionConditionalThenBlock.forEach(stringJClass, "a", commandLineVar
                            .invoke("getOptionValues").arg(field.getName()));
                    JBlock paramForEachBody = paramForEach.body();

                    paramForEachBody.add(paramVar.invoke("add").arg(JExpr._new(typeJClass).arg(paramForEach.var())));
                    hasOptionConditionalThenBlock.add(applicationVar.invoke(
                            "set" + StringUtils.capitalize(field.getName())).arg(paramVar));

                } else if (field.getType() == Boolean.class) {

                    hasOptionConditionalThenBlock.add(applicationVar.invoke(
                            "set" + StringUtils.capitalize(field.getName())).arg(booleanJClass.staticRef("TRUE")));

                } else {
                    paramVar = hasOptionConditionalThenBlock.decl(typeClass, field.getName());
                    paramVar.init(typeClass.staticInvoke("valueOf").arg(
                            commandLineVar.invoke("getOptionValue").arg(field.getName())));
                    hasOptionConditionalThenBlock.add(applicationVar.invoke(
                            "set" + StringUtils.capitalize(field.getName())).arg(paramVar));
                }

            }
        }

        JVar appCLIVar = tryBlockBody.decl(cliClass, "cliApp");
        appCLIVar.init(JExpr._new(cliClass).arg(applicationVar));
        tryBlockBody.add(appCLIVar.invoke("run"));

        JCatchBlock catchBlock = tryBlock._catch(parseExceptionJClass);
        JVar exceptionVar = catchBlock.param("e");
        JBlock catchBlockBody = catchBlock.body();
        catchBlockBody.add(systemJClass.staticRef("err").invoke("println")
                .arg(JExpr.lit("Parsing Failed: ").plus(exceptionVar.invoke("getMessage"))));
        catchBlockBody.add(helpFormatterVar.invoke("printHelp").arg(clazz.getSimpleName() + "CLI")
                .arg(cliOptionsFieldVar));

        catchBlockBody.add(systemJClass.staticInvoke("exit").arg(JExpr.lit(-1)));

        catchBlock = tryBlock._catch(exceptionJClass);
        exceptionVar = catchBlock.param("e");
        catchBlockBody = catchBlock.body();
        catchBlockBody.add(exceptionVar.invoke("printStackTrace"));
        catchBlockBody.add(systemJClass.staticInvoke("exit").arg(JExpr.lit(-1)));

    }

    /*
     * intended for testing...not real world use
     */
    public static void main(String[] args) {
        ReflectionManager reflectionManager = ReflectionManager.getInstance();

        List<Class<?>> filteredClassList = new ArrayList<Class<?>>();
        List<Class<?>> classList = new ArrayList<Class<?>>();

        String pkg = "edu.unc.mapseq.module";
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

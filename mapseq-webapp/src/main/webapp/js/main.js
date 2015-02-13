var monthNames = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ];

var app = angular.module('mapseq', [ 'ngResource', 'ngRoute', 'angularCharts' ]);

app.config([ '$routeProvider', function($routeProvider) {
  $routeProvider

  .when("/", { templateUrl : "home.html", controller : "PageCtrl" })

  .when("/reports/weekly", { templateUrl : "reports/weekly.html", controller : "weeklyWorkflowRunReportCtrl" })

  .when("/reports/monthly", { templateUrl : "reports/monthly.html", controller : "monthlyWorkflowRunReportCtrl" })

  .otherwise("/404", { templateUrl : "404.html", controller : "PageCtrl" });

} ]);

app.controller('PageCtrl', function(/* $scope, $location, $http */) {
  console.log("Page Controller reporting for duty.");
});

function formatDate(date) {
  var day = date.getDate();
  var month = date.getMonth() + 1;
  var year = date.getFullYear();
  if (month < 10) {
    month = "0" + month;
  }
  if (day < 10) {
    day = "0" + day;
  }
  return year + "-" + month + "-" + day;
};

app.controller('weeklyWorkflowRunReportCtrl', function($scope, $resource) {

  var currentDate = new Date();
  var finished = formatDate(currentDate);
  currentDate.setDate(currentDate.getDate() - 7);
  var started = formatDate(currentDate);

  $scope.weeklyWorkflows = [];
  $scope.weeklySelectedWorkflow = {};

  $scope.weeklyWorkflowRunCountData = { series : [], data : [] };
  $scope.weeklyWorkflowRunDurationData = { series : [], data : [] };
  $scope.weeklyWorkflowRunCountBarChartData = { series : [], data : [] };

  $scope.weeklyWorkflowRunCountConfig = { title : "Weekly WorkflowRunAttempt Count", labels : false, tooltips : true,
    legend : { display : false, htmlEnabled : true, position : 'right' }, lineLegend : 'traditional' };
  $scope.weeklyWorkflowRunDurationConfig = { title : "Weekly WorkflowRunAttempt Duration", labels : false, tooltips : true,
    legend : { display : false, htmlEnabled : true, position : 'right' }, lineLegend : 'traditional' };
  $scope.weeklyWorkflowRunCountBarChartConfig = { title : $scope.weeklySelectedWorkflow.name, labels : false, tooltips : true,
    legend : { display : false, htmlEnabled : true, position : 'right' }, lineLegend : 'traditional' };

  var workflowServiceResource = $resource('/cxf/Workflow/WorkflowService/findAll');
  var workflowServiceResourceQuery = workflowServiceResource.query();

  var workflowRunAttemptResource = $resource(
      '/cxf/WorkflowRunAttempt/WorkflowRunAttemptService/findByCreatedDateRangeAndWorkflowIdAndStatus/:started/:finished/:workflowId/DONE', {
        started : '@started', finished : '@finished', workflowId : '@workflowId' });

  workflowServiceResourceQuery.$promise.then(function(workflowsData) {

    $scope.weeklySelectedWorkflow = workflowsData[0];
    $scope.updateWeeklyWorkflowRunBarChart();

    angular.forEach(workflowsData, function(workflowData) {
      var workflowRunAttemptResourceQuery = workflowRunAttemptResource.query({ started : started, finished : finished, workflowId : workflowData.id });
      workflowRunAttemptResourceQuery.$promise.then(function(workflowRunAttemptsData) {
        if (workflowRunAttemptsData.length > 0) {
          
          $scope.weeklyWorkflows.push(workflowData);

          $scope.weeklyWorkflowRunCountData.series.push(workflowData.name);
          $scope.weeklyWorkflowRunCountData.data.push({ "x" : workflowData.name, "y" : [ workflowRunAttemptsData.length ],
            "tooltip" : workflowData.name + ": " + workflowRunAttemptsData.length });

          $scope.weeklyWorkflowRunDurationData.series.push(workflowData.name);
          var duration = 0;
          angular.forEach(workflowRunAttemptsData, function(workflowRunAttemptData) {
            var startDate = new Date(workflowRunAttemptData.started);
            var finishedDate = new Date(workflowRunAttemptData.finished);
            var started = startDate.getTime();
            var finished = finishedDate.getTime();
            duration += parseInt(finished - started);
          });
          $scope.weeklyWorkflowRunDurationData.data.push({ "x" : workflowData.name, "y" : [ duration ],
            "tooltip" : workflowData.name + ": " + Math.round(duration / (60 * 60 * 1000)) + " hours" });
        }
      });
    });
  });

  $scope.updateWeeklyWorkflowRunBarChart = function() {

    var workflowRunAttemptResourceQuery = workflowRunAttemptResource.query({ started : started, finished : finished,
      workflowId : $scope.weeklySelectedWorkflow.id });

    $scope.weeklyWorkflowRunCountBarChartData.series = [];
    $scope.weeklyWorkflowRunCountBarChartData.data = [];

    workflowRunAttemptResourceQuery.$promise.then(function(workflowRunAttemptsData) {

      angular.forEach(workflowRunAttemptsData, function(workflowRunAttemptData) {
        var dateCreated = new Date(workflowRunAttemptData.created);
        var display = monthNames[dateCreated.getMonth()] + " " + dateCreated.getDate();
        if ($scope.weeklyWorkflowRunCountBarChartData.series.indexOf(display) == -1) {
          $scope.weeklyWorkflowRunCountBarChartData.series.push(display);
          $scope.weeklyWorkflowRunCountBarChartData.data.push({ "x" : display, "y" : [ 0 ] });
        }
      });

      angular.forEach(workflowRunAttemptsData, function(workflowRunAttemptData) {
        var dateCreated = new Date(workflowRunAttemptData.created);
        var display = monthNames[dateCreated.getMonth()] + " " + dateCreated.getDate();

        for (i = 0; i < $scope.weeklyWorkflowRunCountBarChartData.data.length; ++i) {
          var x = $scope.weeklyWorkflowRunCountBarChartData.data[i].x;
          if (angular.equals(x, display)) {
            $scope.weeklyWorkflowRunCountBarChartData.data[i].y[0] += 1;
          }
        }

      });

    });
  };

});

app.controller('monthlyWorkflowRunReportCtrl', function($scope, $resource) {

  var currentDate = new Date();
  var finished = formatDate(currentDate);
  currentDate.setDate(currentDate.getDate() - 30);
  var started = formatDate(currentDate);

  $scope.monthlyWorkflows = [];
  $scope.monthlySelectedWorkflow = {};

  $scope.monthlyWorkflowRunCountData = { series : [], data : [] };
  $scope.monthlyWorkflowRunDurationData = { series : [], data : [] };
  $scope.monthlyWorkflowRunCountBarChartData = { series : [], data : [] };

  $scope.monthlyWorkflowRunCountConfig = { title : "Monthly WorkflowRunAttempt Count", labels : false, tooltips : true,
    legend : { display : false, htmlEnabled : true, position : 'right' }, lineLegend : 'traditional' };
  $scope.monthlyWorkflowRunDurationConfig = { title : "Monthly WorkflowRunAttempt Duration", labels : false, tooltips : true,
    legend : { display : false, htmlEnabled : true, position : 'right' }, lineLegend : 'traditional' };
  $scope.monthlyWorkflowRunCountBarChartConfig = { title : $scope.monthlySelectedWorkflow.name, labels : false, tooltips : true,
    legend : { display : false, htmlEnabled : true, position : 'right' }, lineLegend : 'traditional' };

  var workflowServiceResource = $resource('/cxf/Workflow/WorkflowService/findAll');
  var workflowServiceResourceQuery = workflowServiceResource.query();

  var workflowRunAttemptResource = $resource(
      '/cxf/WorkflowRunAttempt/WorkflowRunAttemptService/findByCreatedDateRangeAndWorkflowIdAndStatus/:started/:finished/:workflowId/DONE', {
        started : '@started', finished : '@finished', workflowId : '@workflowId' });

  workflowServiceResourceQuery.$promise.then(function(workflowsData) {

    $scope.monthlySelectedWorkflow = workflowsData[0];
    $scope.updateMonthlyWorkflowRunBarChart();

    angular.forEach(workflowsData, function(workflowData) {
      var workflowRunAttemptResourceQuery = workflowRunAttemptResource.query({ started : started, finished : finished, workflowId : workflowData.id });
      workflowRunAttemptResourceQuery.$promise.then(function(workflowRunAttemptsData) {
        if (workflowRunAttemptsData.length > 0) {
          $scope.monthlyWorkflows.push(workflowData);
          $scope.monthlyWorkflowRunCountData.series.push(workflowData.name);
          $scope.monthlyWorkflowRunCountData.data.push({ "x" : workflowData.name, "y" : [ workflowRunAttemptsData.length ],
            "tooltip" : workflowData.name + ": " + workflowRunAttemptsData.length });
          $scope.monthlyWorkflowRunDurationData.series.push(workflowData.name);
          var duration = 0;
          angular.forEach(workflowRunAttemptsData, function(workflowRunAttemptData) {
            var startDate = new Date(workflowRunAttemptData.started);
            var finishedDate = new Date(workflowRunAttemptData.finished);
            var started = startDate.getTime();
            var finished = finishedDate.getTime();
            duration += parseInt(finished - started);
          });
          $scope.monthlyWorkflowRunDurationData.data.push({ "x" : workflowData.name, "y" : [ duration ],
            "tooltip" : workflowData.name + ": " + Math.round(duration / (60 * 60 * 1000)) + " hours" });
        }
      });
    });
  });

  $scope.updateMonthlyWorkflowRunBarChart = function() {

    var workflowRunAttemptResourceQuery = workflowRunAttemptResource.query({ started : started, finished : finished,
      workflowId : $scope.monthlySelectedWorkflow.id });

    $scope.monthlyWorkflowRunCountBarChartData.series = [];
    $scope.monthlyWorkflowRunCountBarChartData.data = [];

    workflowRunAttemptResourceQuery.$promise.then(function(workflowRunAttemptsData) {

      angular.forEach(workflowRunAttemptsData, function(workflowRunAttemptData) {
        var dateCreated = new Date(workflowRunAttemptData.created);
        var display = monthNames[dateCreated.getMonth()] + " " + dateCreated.getDate();
        if ($scope.monthlyWorkflowRunCountBarChartData.series.indexOf(display) == -1) {
          $scope.monthlyWorkflowRunCountBarChartData.series.push(display);
          $scope.monthlyWorkflowRunCountBarChartData.data.push({ "x" : display, "y" : [ 0 ] });
        }
      });

      angular.forEach(workflowRunAttemptsData, function(workflowRunAttemptData) {
        var dateCreated = new Date(workflowRunAttemptData.created);
        var display = monthNames[dateCreated.getMonth()] + " " + dateCreated.getDate();

        for (i = 0; i < $scope.monthlyWorkflowRunCountBarChartData.data.length; ++i) {
          var x = $scope.monthlyWorkflowRunCountBarChartData.data[i].x;
          if (angular.equals(x, display)) {
            $scope.monthlyWorkflowRunCountBarChartData.data[i].y[0] += 1;
          }
        }

      });

    });
  };

});

var monthNames = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ];

var app = angular.module('mapseq', [ 'ngResource', 'ngRoute', 'angularCharts' ]);

app.config([ '$routeProvider', function($routeProvider) {
  $routeProvider

  .when("/", { templateUrl : "home.html", controller : "PageCtrl" })

  .when("/reports/week", { templateUrl : "reports/week.html", controller : "weekWorkflowRunReportCtrl" })

  .when("/reports/month", { templateUrl : "reports/month.html", controller : "monthWorkflowRunReportCtrl" })

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

app.controller('weekWorkflowRunReportCtrl', function($scope, $resource) {

  var currentDate = new Date();
  var finished = formatDate(currentDate);
  currentDate.setDate(currentDate.getDate() - 7);
  var started = formatDate(currentDate);

  $scope.weekWorkflowRunCountPieChartData = { series : [], data : [] };
  $scope.weekWorkflowRunCountBarChartData = { series : [], data : [] };

  $scope.weekWorkflowRunCountPieChartConfig = { click : function(d) {
    $scope.updateWeekWorkflowRunCountBarChart(d);
  }, labels : false, tooltips : true, legend : { display : false, htmlEnabled : true, position : 'left' }, lineLegend : 'traditional' };
  $scope.weekWorkflowRunCountBarChartConfig = { labels : false, tooltips : true, legend : { display : false, htmlEnabled : true, position : 'left' },
    lineLegend : 'traditional' };

  $scope.weekWorkflowRunDurationPieChartData = { series : [], data : [] };
  $scope.weekWorkflowRunDurationBarChartData = { series : [], data : [] };

  $scope.weekWorkflowRunDurationPieChartConfig = { labels : false, tooltips : true, legend : { display : false, htmlEnabled : true, position : 'left' },
    lineLegend : 'traditional' };
  $scope.weekWorkflowRunDurationBarChartConfig = { labels : false, tooltips : true, legend : { display : false, htmlEnabled : true, position : 'left' },
    lineLegend : 'traditional' };

  var workflowServiceResource = $resource('/cxf/Workflow/WorkflowService/findAll');
  var workflowServiceResourceQuery = workflowServiceResource.query();

  var workflowRunAttemptResource = $resource(
      '/cxf/WorkflowRunAttempt/WorkflowRunAttemptService/findByCreatedDateRangeAndWorkflowIdAndStatus/:started/:finished/:workflowId/DONE', {
        started : '@started', finished : '@finished', workflowId : '@workflowId' });

  workflowServiceResourceQuery.$promise.then(function(workflowsData) {

    angular.forEach(workflowsData, function(workflowData) {

      var workflowRunAttemptResourceQuery = workflowRunAttemptResource.query({ started : started, finished : finished, workflowId : workflowData.id });

      workflowRunAttemptResourceQuery.$promise.then(function(workflowRunAttemptsData) {
        
        if (workflowRunAttemptsData.length > 0) {

          $scope.weekWorkflowRunCountPieChartData.series.push(workflowData.name);
          $scope.weekWorkflowRunCountPieChartData.data.push({ "x" : workflowData.name, "y" : [ workflowRunAttemptsData.length ],
            "tooltip" : workflowData.name + ": " + workflowRunAttemptsData.length });

          $scope.weekWorkflowRunDurationPieChartData.series.push(workflowData.name);

          var date = new Date();
          date.setDate(date.getDate() - 7);

          for (i = 0; i < 7; i++) {
            date.setDate(date.getDate() + 1);
            var display = monthNames[date.getMonth()] + " " + date.getDate();
            $scope.weekWorkflowRunCountBarChartData.series.push(display);
            $scope.weekWorkflowRunCountBarChartData.data.push({ "x" : display, "y" : [ 0 ] });
          }

          var duration = 0;
          angular.forEach(workflowRunAttemptsData, function(workflowRunAttemptData) {
            var startDate = new Date(workflowRunAttemptData.started);
            var finishedDate = new Date(workflowRunAttemptData.finished);
            duration += parseInt(finishedDate.getTime() - startDate.getTime());
          });
          
          $scope.weekWorkflowRunDurationPieChartData.data.push({ "x" : workflowData.name, "y" : [ duration ],
            "tooltip" : workflowData.name + ": " + Math.round(duration / (60 * 60 * 1000)) + " hours" });

          $scope.weekWorkflowRunDurationBarChartData.series.push(workflowData.name);
          $scope.weekWorkflowRunDurationBarChartData.data.push({ "x" : workflowData.name, "y" : [ Math.round(duration / (60 * 60 * 1000)) ] });

        }
      });
    });

  });

  $scope.updateWeekWorkflowRunCountBarChart = function(d) {

    var currentDate = new Date();
    var finished = formatDate(currentDate);
    currentDate.setDate(currentDate.getDate() - 7);
    var started = formatDate(currentDate);

    if (angular.isDefined(d) && angular.isDefined(d.data) && angular.isDefined(d.data.x)) {

      var workflowNameResource = $resource('/cxf/Workflow/WorkflowService/findByName/:name', { name : '@name' });
      var workflowNameResourceQuery = workflowNameResource.query({ name : d.data.x });

      workflowNameResourceQuery.$promise.then(function(workflowsData) {

	  angular.forEach(workflowsData, function(workflowData) {

	      var workflowRunAttemptResource = $resource(
		  '/cxf/WorkflowRunAttempt/WorkflowRunAttemptService/findByCreatedDateRangeAndWorkflowIdAndStatus/:started/:finished/:workflowId/DONE', {
                      started : '@started', finished : '@finished', workflowId : '@workflowId' });
	      
              var workflowRunAttemptResourceQuery = workflowRunAttemptResource.query({ started : started, finished : finished,
										       workflowId : workflowData.id });
	      
              $scope.weekWorkflowRunCountBarChartData.series = [];
              $scope.weekWorkflowRunCountBarChartData.data = [];

              workflowRunAttemptResourceQuery.$promise.then(function(workflowRunAttemptsData) {
		  
		  var date = new Date();
		  date.setDate(date.getDate() - 7);
		  
		  for (i = 0; i < 7; i++) {
		      date.setDate(date.getDate() + 1);
		      var display = monthNames[date.getMonth()] + " " + date.getDate();
		      $scope.weekWorkflowRunCountBarChartData.series.push(display);
		      $scope.weekWorkflowRunCountBarChartData.data.push({ "x" : display, "y" : [ 0 ] });
		  }
		  
		  angular.forEach(workflowRunAttemptsData, function(workflowRunAttemptData) {
		      var dateCreated = new Date(workflowRunAttemptData.created);
		      var display = monthNames[dateCreated.getMonth()] + " " + dateCreated.getDate();
		      
		      for (i = 0; i < $scope.weekWorkflowRunCountBarChartData.data.length; ++i) {
			  var x = $scope.weekWorkflowRunCountBarChartData.data[i].x;
			  if (angular.equals(x, display)) {
			      $scope.weekWorkflowRunCountBarChartData.data[i].y[0] += 1;
			  }
		      }
		      
		  });
		  
              });

	  });

      });

    }

  };

});

app.controller('monthWorkflowRunReportCtrl', function($scope, $resource) {

  var currentDate = new Date();
  var finished = formatDate(currentDate);
  currentDate.setDate(currentDate.getDate() - 30);
  var started = formatDate(currentDate);

  $scope.monthWorkflowRunCountPieChartData = { series : [], data : [] };
  $scope.monthWorkflowRunCountBarChartData = { series : [], data : [] };

  $scope.monthWorkflowRunCountPieChartConfig = { click : function(d) {
    $scope.updateMonthWorkflowRunCountBarChart(d);
  }, labels : false, tooltips : true, legend : { display : false, htmlEnabled : true, position : 'left' }, lineLegend : 'traditional' };
  $scope.monthWorkflowRunCountBarChartConfig = { labels : false, tooltips : true, legend : { display : false, htmlEnabled : true, position : 'left' },
    lineLegend : 'traditional' };

  $scope.monthWorkflowRunDurationPieChartData = { series : [], data : [] };
  $scope.monthWorkflowRunDurationBarChartData = { series : [], data : [] };

  $scope.monthWorkflowRunDurationPieChartConfig = { labels : false, tooltips : true, legend : { display : false, htmlEnabled : true, position : 'left' },
    lineLegend : 'traditional' };
  $scope.monthWorkflowRunDurationBarChartConfig = { labels : false, tooltips : true, legend : { display : false, htmlEnabled : true, position : 'left' },
    lineLegend : 'traditional' };

  var workflowServiceResource = $resource('/cxf/Workflow/WorkflowService/findAll');
  var workflowServiceResourceQuery = workflowServiceResource.query();

  var workflowRunAttemptResource = $resource(
      '/cxf/WorkflowRunAttempt/WorkflowRunAttemptService/findByCreatedDateRangeAndWorkflowIdAndStatus/:started/:finished/:workflowId/DONE', {
        started : '@started', finished : '@finished', workflowId : '@workflowId' });

  workflowServiceResourceQuery.$promise.then(function(workflowsData) {

    angular.forEach(workflowsData, function(workflowData) {
      var workflowRunAttemptResourceQuery = workflowRunAttemptResource.query({ started : started, finished : finished, workflowId : workflowData.id });
      workflowRunAttemptResourceQuery.$promise.then(function(workflowRunAttemptsData) {
        if (workflowRunAttemptsData.length > 0) {

          $scope.monthWorkflowRunCountPieChartData.series.push(workflowData.name);
          $scope.monthWorkflowRunCountPieChartData.data.push({ "x" : workflowData.name, "y" : [ workflowRunAttemptsData.length ],
            "tooltip" : workflowData.name + ": " + workflowRunAttemptsData.length });

          $scope.monthWorkflowRunDurationPieChartData.series.push(workflowData.name);

          var date = new Date();
          date.setDate(date.getDate() - 30);

          for (i = 0; i < 30; i++) {
            date.setDate(date.getDate() + 1);
            var display = (date.getMonth() + 1) + "-" + date.getDate();

            $scope.monthWorkflowRunCountBarChartData.series.push(display);
            $scope.monthWorkflowRunCountBarChartData.data.push({ "x" : display, "y" : [ 0 ] });
          }

          var duration = 0;
          angular.forEach(workflowRunAttemptsData, function(workflowRunAttemptData) {
            var startDate = new Date(workflowRunAttemptData.started);
            var finishedDate = new Date(workflowRunAttemptData.finished);
            duration += parseInt(finishedDate.getTime() - startDate.getTime());
          });

          $scope.monthWorkflowRunDurationPieChartData.data.push({ "x" : workflowData.name, "y" : [ duration ],
            "tooltip" : workflowData.name + ": " + Math.round(duration / (60 * 60 * 1000)) + " hours" });

          $scope.monthWorkflowRunDurationBarChartData.series.push(workflowData.name);
          $scope.monthWorkflowRunDurationBarChartData.data.push({ "x" : workflowData.name, "y" : [ Math.round(duration / (60 * 60 * 1000)) ] });
        }
      });
    });
  });

  $scope.updateMonthWorkflowRunCountBarChart = function(d) {

    var currentDate = new Date();
    var finished = formatDate(currentDate);
    currentDate.setDate(currentDate.getDate() - 30);
    var started = formatDate(currentDate);

    if (angular.isDefined(d) && angular.isDefined(d.data) && angular.isDefined(d.data.x)) {

      var workflowNameResource = $resource('/cxf/Workflow/WorkflowService/findByName/:name', { name : '@name' });
      var workflowNameResourceQuery = workflowNameResource.query({ name : d.data.x });

      workflowNameResourceQuery.$promise.then(function(workflowsData) {

        angular.forEach(workflowsData, function(workflowData) {

          var workflowRunAttemptResource = $resource(
              '/cxf/WorkflowRunAttempt/WorkflowRunAttemptService/findByCreatedDateRangeAndWorkflowIdAndStatus/:started/:finished/:workflowId/DONE', {
                started : '@started', finished : '@finished', workflowId : '@workflowId' });

          var workflowRunAttemptResourceQuery = workflowRunAttemptResource.query({ started : started, finished : finished, workflowId : workflowData.id });

          $scope.monthWorkflowRunCountBarChartData.series = [];
          $scope.monthWorkflowRunCountBarChartData.data = [];

          workflowRunAttemptResourceQuery.$promise.then(function(workflowRunAttemptsData) {

            var date = new Date();
            date.setDate(date.getDate() - 30);

            for (i = 0; i < 30; i++) {
              date.setDate(date.getDate() + 1);
              var display = (date.getMonth() + 1) + "-" + date.getDate();
              $scope.monthWorkflowRunCountBarChartData.series.push(display);
              $scope.monthWorkflowRunCountBarChartData.data.push({ "x" : display, "y" : [ 0 ] });
            }

            angular.forEach(workflowRunAttemptsData, function(workflowRunAttemptData) {
              var dateCreated = new Date(workflowRunAttemptData.created);
              var display = (dateCreated.getMonth() + 1) + "-" + dateCreated.getDate();

              for (i = 0; i < $scope.monthWorkflowRunCountBarChartData.data.length; ++i) {
                var x = $scope.monthWorkflowRunCountBarChartData.data[i].x;
                if (angular.equals(x, display)) {
                  $scope.monthWorkflowRunCountBarChartData.data[i].y[0] += 1;
                }
              }

            });

          });
        });

      });

    }

  };

});

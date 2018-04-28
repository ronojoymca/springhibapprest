<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII" errorPage="error.jsp"%>
<html lang="en">
<%
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", -1);
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
	<head>
		<title><spring:message code="login.welcome" text="default text" /></title>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link href="https://fonts.googleapis.com/css?family=Roboto:400,500,700" rel="stylesheet">
		<link href="static/frameworks/bootstrap/css/bootstrap.min.css" rel="stylesheet">
		<link href="static/styles/core.css" rel="stylesheet">
		<script src="static/scripts/Common.js"></script>
	</head>
	<body>
	
		<nav class="navbar navbar-default eks-flat-corner">
			<div class="container-fluid">
				<div class="navbar-header">
					<img src="static/images/mycom-logo.png" class="pull-left img-responsive" style="width:65px"/>
					<a class="navbar-brand" href="#"><spring:message code="dashboard.surveyApp" text="default text" /></a>
				</div>
			</div>
		</nav>
		<div class="container-fluid">
			<div class="row">
				<div class="col-md-4 col-md-offset-4">
				<div class="alert alert-danger" id="login-error-alert" style="visibility:hidden"> <strong>Error:</strong>Invalid Credentials: </div>
					<div class="panel panel-primary eks-login-panel" style="width:100%">
						<div class="panel-heading"><spring:message code="login.login" text="default text" /></div>
						<div class="panel-body">
							<form>
							  <div class="form-group">
								<label for="email"><spring:message code="login.email" text="default text" />:</label>
								<input type="email" class="form-control" id="email">
							  </div>
							  <div class="form-group">
								<label for="pwd"><spring:message code="login.password" text="default text" />:</label>
								<input type="password" class="form-control" id="pwd">
								
							  </div>
							  <div class="checkbox">
								<label><input type="checkbox" name="remPwd" id="remPwd"> <spring:message code="login.rememberMe" text="default text" /></label>
							  </div>
							  <a onClick="login()" class="btn btn-primary" style="background-color: #337ab7; border-color: #337ab7"><spring:message code="login.submit" text="default text" /></a>			  
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
		<script src="static/frameworks/jquery/jquery-3.1.1.js"></script>
        <script src="static/frameworks/bootstrap/js/bootstrap.min.js"></script>
		<script type="text/javascript">
		function showLoginErrorAlert(){
			$("#btn-submit").prop('disabled', true); 
			$("#login-error-alert").css("opacity", 1); 
			$("#login-error-alert").css("visibility", "visible");
            $("#login-error-alert").fadeTo(2000, 0, function(){
            	 $("#btn-submit").prop('disabled', false); }); 
            }
		$(":checkbox").change(function(){
		     $(this).val($(this).is(":checked") ? 1 : 0);
		});
        function login(){
        	
            var loginDetails = {
                        "loginId": $("#email").val(),
                        "password": $("#pwd").val(),
                        "remPwd":$("#remPwd").val()
                    };
            $.ajax({
                url: "login",
                data: JSON.stringify(loginDetails),
                error: function(e){
					showLoginErrorAlert();
                },
                success: function(data){
                   if(data.error)
						showLoginErrorAlert();
                   else{
                	   window.location.href="dashboard";
                   }   
                },
                dataType: "json",
                contentType: 'application/json; charset=utf-8',
                type: "POST",
                cache: false,
                crossDomain: true
            });
        }
		</script>
	</body>
	
</html>
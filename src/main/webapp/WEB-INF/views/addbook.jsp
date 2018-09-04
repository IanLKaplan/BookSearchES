<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" 
	     rel="stylesheet" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" 
	     crossorigin="anonymous">

    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" 
            integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" 
            crossorigin="anonymous"></script>

    <link rel="stylesheet" href="resources/css/kube-6.5.2.min.css">
    
    <link rel="stylesheet" href="resources/css/main.css">
    
    <style>
    div.box {
       border: 1px solid;
       padding: 10px;
    }
    div.error {
    	color:red;
    }
    </style>
    
<title>Book Search: Add a new book</title>
</head>
<body>
	<div class="container">
		<div style="padding-top:1em;"></div>
		<h2 align="center">Book Search: Add Books</h2>
		<div style="padding-top:1em;"></div>
		<c:if test="${fields.hasErrors()}">
		<div class="row">
				<div class="col-md-2"></div>
				<div class="col-md-8">
					<div class="box">"${author}"</div>
				</div>
				<div class="col-md-2"></div>
			</div>
		</c:if>
		<div class="row">
			<div class="col-md-12">
				<ul class="nav nav-tabs">
					<li class="nav-item">
						<a id="explore" class="nav-link" href="<c:url value="/" />">Explore the Book Database</a>
					</li>
					<li class="nav-item">
						<a id="search" class="nav-link" href="<c:url value="/search" />">Search for a book</a>
					</li>
					<li class="nav-item">
					    <a id="addbook" class="nav-link active" href="<c:url value="#" />">Add a book</a>
					</li>
				</ul>
			  </div>
		</div> <!-- row -->
	    <div style="padding-top:1em;"></div>
		<c:if test="${book_saved != null && book_saved.length() > 0}">
		<div class="row">
				<div class="col-md-2"></div>
				<div class="col-md-8">
					<div class="box">${book_saved}</div>
				</div>
				<div class="col-md-2"></div>
			</div>
		</c:if>
		<div class="row">
			<div class="col-md-2"></div>
			<form action="save-book" method="post">
				<div class="form-group row">
				 <legend class="col-form-label col-sm-12"><h3>Enter Book information</h3></legend>
				</div>
				<div class="form-group row">
					<label for="title" class="col-sm-4 col-form-label">Title</label>
					<div class="col-sm-8">
						<input type="text" id="title" name="title" placeholder="Title">
						<c:if test="${errors.hasFieldErrors('title')}">
						   <div class="error">
							${errors.getFieldError("title").getDefaultMessage()}
							</div>
	      				</c:if>
					</div> <!-- col-sm-8 -->
				</div>
				<!--  form-group row -->

				<div class="form-group row">
					<label for="author" class="col-sm-4 col-form-label">Author</label>
					<div class="col-sm-8">
						<input type="text" id="author" name="author" placeholder="Author">
						   <c:if test="${errors.hasFieldErrors('author')}">
						       <div class="error">
							       ${errors.getFieldError("author").getDefaultMessage()}
							    </div>
	      				</c:if>
					</div>
					<!-- col-sm-8 -->
				</div>
				<!-- form-group row -->
				
				<div class="form-group row">
					<label for="genre" class="col-sm-4 col-form-label">Genre</label>
					<select class="custom-select col-sm-8" name="genre" id="genre">
        				<option selected>Genre</option>
        				<option value="Science Fiction">Science Fiction</option>
        				<option value="Fiction">Fiction</option>
        			    <option value="History">History</option>
        			    <option value="Computer Science">Computer Science</option>
        			    <option value="Finance">Finance</option>
        			    <option value="Mathematics">Mathematics</option>
        			    <option value="Current Events">Current Events</option>
        			    <option value="Science">Science</option>
        			    <option value="Cooking">Cooking</option>
        			    <option value="Travel">Travel</option>
        			    <option value="Nonfiction">Nonfiction</option>
      				</select>
					<!-- col-sm-8 -->
				</div>
				<!-- form-group row -->

				<div class="form-group row">
					<label for="publisher" class="col-sm-4 col-form-label">Publisher</label>
					<div class="col-sm-6">
						<input type="text" id="publisher" name="publisher" placeholder="Publisher">
						<c:if test="${errors.hasFieldErrors('publisher')}">
						   <div class="error">
							  ${errors.getFieldError("publisher").getDefaultMessage()}
							</div>
	      				</c:if>
					</div>
					<!-- col-sm-6 -->
					<div class="col-sm-2"></div>
				</div>
				<!-- form-group row -->

				<div class="form-group row">
					<label for="year" class="col-sm-4 col-form-label">Year</label>
					<div class="col-sm-6">
						<input type="number" id="year" name="year" placeholder="Year">
						   <c:if test="${errors.hasFieldErrors('year')}">
						      <div class="error">
							      ${errors.getFieldError("year").getDefaultMessage()}
							   </div>
	      				</c:if>
					</div>
					<!-- col-sm-6 -->
					<div class="col-sm-2"></div>
				</div>
				<!-- form-group row -->

				<div class="form-group row">
					<label for="price" class="col-sm-4 col-form-label">Price</label>
					<div class="col-sm-6">
						<input type="text" id="price" name="price" placeholder="Price">
						<c:if test="${errors.hasFieldErrors('price')}">
						   <div class="error">
							${errors.getFieldError("price").getDefaultMessage()}
							</div>
	      				</c:if>
					</div>
					<!-- col-sm-6 -->
					<div class="col-sm-2"></div>
				</div>
				<!-- form-group row -->

				<div class="form-group row">
					<div class="col-sm-4">
						<button type="submit" class="btn btn-primary">Add Book</button>
					</div>
					<div class="col-sm-4">
						<button type="reset" class="btn btn-primary">Reset</button>
					</div>
				</div>

			</form>
			<div class="col-md-2"></div>
		</div>
		<!--  row -->
	</div>
	<!-- container -->
</body>
</html>
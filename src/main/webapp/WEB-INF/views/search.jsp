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
      div.bold {  font-weight: bold; }
      
      div.box {
       border: 1px solid;
       padding: 10px;
      }
      
      span.errorSpan {
      	color:red;
      }
    </style>

<title>Book Search Demo Application (with Elasticsearch)</title>
</head>
<body>
<div class="container">
		<div style="padding-top:1em;"></div>
		<h2 align="center">Book Search: Search</h2>
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
						<a id="search" class="nav-link active" href="<c:url value="#" />">Search for a book</a>
					</li>
					<li class="nav-item">
					    <a id="addbook" class="nav-link" href="<c:url value="/addbook" />">Add a book</a>
					</li>
				</ul>
			  </div>
		</div> <!-- row -->
	    <div style="padding-top:1em;"></div>
	    <div class="row">
	    	<div class="col-md-1"></div>
			<div class="col-md-11 bold">
			    Search by Title/Author
		    </div>
		</div>
		<div class="row">
			<div class="col-md-1"></div>
			<div class="col-md-11">
				<form action="title-author-search" method="post">
					<div class="form-group row">
						<div class="col-sm-2">
							<button type="submit" class="btn btn-primary">Search</button>
						</div>
						<div class="col-sm-5 " style="width:100%;">
						    <span>
							<input type="text" class="form-control" id="title" name="title" placeholder="Title">
							</span>
							<c:if test="${title_author_title_error != null && title_author_title_error.length() > 0}">
								<span class="errorSpan">${title_author_title_error}</span>
							</c:if>
						</div>
						<div class="col-sm-5">
						    <span>
							<input type="text" class="form-control" id="author" name="author" placeholder="Author">
							</span>
							<c:if test="${title_author_author_error != null && title_author_author_error.length() > 0}">
								<span class="errorSpan">${title_author_author_error}</span>
							</c:if>
						</div>
					</div>
				</form>
			</div>
		</div>
		<!-- row -->
		<div class="row">
			    <div class="col-md-1"></div>
			    <div class="col-md-11 bold">
			    Search by Author
			    </div>
		</div>
		<div class="row">
			<div class="col-md-1"></div>
			<div class="col-md-11">
				<form action="author-search" method="post">
					<div class="form-group row">
						<div class="col-sm-2">
							<button type="submit" class="btn btn-primary">Search</button>
						</div>
						<div class="col-sm-5">
							<input type="text" class="form-control" id="author" name="author"
								placeholder="Author name">
						</div>
						<div class="col-sm-6"></div>
					</div>
				</form>
			</div>
		</div>
		<!-- row -->
		<div class="row">
			    <div class="col-md-1"></div>
			    <div class="col-md-11 bold">
			       Search by Title 
			    </div>
		</div>
		<div class="row">
			<div class="col-md-1"></div>
			<div class="col-md-11">
				<form action="title-search" method="post">
					<div class="form-group row">
						<div class="col-sm-2">
							<button type="submit" class="btn btn-primary">Search</button>
						</div>
						<div class="col-sm-5">
							<input type="text" class="form-control" id="title" name="title"
								placeholder="Title">
						</div>
						<div class="col-sm-5"></div>
					</div>
				</form>
			</div>
		</div> <!-- row -->
		<div style="padding-top:1em;"></div>
		<div class="row">
			<div class="col-md-1"></div>
			<div class="col-md-11">
				<form action="list-all-books" method="post">
					<button type="submit" class="btn btn-primary">List all books</button>
				</form>
			</div>
		</div> <!-- row -->
		<c:if test="${bookList != null && bookList.size() > 0}">
			<div style="padding-top:1em;"></div>
			<div class="row">
				<div class="col-md-1"></div>
				<div class="col-md-11">
					<table class="table">
					   <thead>
						<tr>
							<th>Title</th>
							<th>Author</th>
							<th>Genre</th>
							<th>Publisher</th>
							<th>Year</th>
							<th>Price</th>
						</tr>
						</thead>
						<tbody>
						<c:forEach var="book" items="${bookList}">
							<tr>
								<td>${book.getTitle()}</td>
								<td>${book.getAuthor()}</td>
								<td>${book.getGenre().toString()}</td>
								<td>${book.getPublisher()}</td>
								<td>${book.getYear()}</td>
								<td>${book.getPrice()}</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
			<!-- row -->
		</c:if>
	</div>
</body>
</html>
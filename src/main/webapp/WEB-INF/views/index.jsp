<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<!-- 
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.12.1/jquery-ui.css"
	integrity="sha256-p6xU9YulB7E2Ic62/PX+h59ayb3PBJ0WFTEQxq0EjHw="
	crossorigin="anonymous" />
	
	 -->

<script src="https://code.jquery.com/jquery-3.3.1.min.js"
	integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
	crossorigin="anonymous">

</script>

<!-- 
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"
	integrity="sha256-VazP97ZCwtekAsvgPBSUwPFKdrwD3unUfSGVYrahUqU="
	crossorigin="anonymous">
</script>
	 -->
	 
<link href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" 
      rel="stylesheet" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" 
      crossorigin="anonymous">	 

<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.bundle.min.js" 
        integrity="sha384-pjaaA8dDz/5BgdFUPX6M/9SUZv4d12SUPF0axWc+VRZkx5xU3daN+lYb49+Ax+Tl" 
        crossorigin="anonymous">
</script>

<link rel="stylesheet" href="resources/css/kube-6.5.2.min.css">

<link rel="stylesheet" href="resources/css/main.css">

<!-- 
<script>
	// The accordian will be collapsed at the start
	$(function() {
		$("#accordion").accordion({
			collapsible : true,
			active : false
		});
	});
</script>
 -->

<style>
div.bold {
	font-weight: bold;
}

div.box {
	border: 1px solid;
	padding: 10px;
}

span.errorSpan {
	color: red;
}
</style>

<title>Book Search Demo Application</title>
</head>
<body>
	<div class="container">
		<div style="padding-top: 1em;"></div>
		<h2 align="center">Book Search: Explore</h2>
		<div style="padding-top: 1em;"></div>
		<div class="row">
			<div class="col-md-12">
				<ul class="nav nav-tabs">
					<li class="nav-item"><a id="explore" class="nav-link active"
						href="<c:url value="#" />">Explore the Book Database</a></li>
					<li class="nav-item"><a id="search" class="nav-link"
						href="<c:url value="/search" />">Search for a book</a></li>
					<li class="nav-item"><a id="addbook" class="nav-link"
						href="<c:url value="/addbook" />">Add a book</a></li>
				</ul>
			</div>
		</div>
		<!-- row -->
	    <div class="row">
	       <div class="col-md-12">
		      <div style="padding-top: 1em;"></div>
		   </div>
		</div> <!-- row -->
		<div class="row">
			<div class="col-md-4">
				<div class="accordion scroll" id="aggregateAccordion">
				    <div class="card">
					<!-- List the book genres as active links -->
					<div class="card-header" id="headingOne">
					<h3 class="mb-0">
					<button class="btn btn-link" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
					Genre
					</button>
					</h3>
					</div> <!-- card-header -->
					<div id="collapseOne" class="collapse" aria-labelledby="headingOne" data-parent="#aggregateAccordion">
					 <div class="card-body">
						<c:if test="${genreAgg != null && genreAgg.size() > 0}">
							<form action="/returnGenreBooks" method="post">
								<!-- a list without bullets -->
								<ul style="list-style: none">
									<c:forEach var="agg" items="${genreAgg}">
										<li><input type="submit" class="btn btn-default"
											id="genre" name="genre" value="${agg.getKey()}">(${agg.getCount()})
										</li>
									</c:forEach>
								</ul>
							</form>
						</c:if>
						</div> <!-- card-body -->
					</div> <!-- collapseOne -->
					</div> <!-- card -->
					 <div class="card">
					 <div class="card-header" id="headingTwo">
					<h3 class="mb-0">
					<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
					Publisher
					</button>
					</h3>
					</div> <!-- card-header -->
					<div id="collapseTwo" class="collapse" aria-labelledby="headingTwo" data-parent="#aggregateAccordion">
					<div class="card-body">
						<c:if test="${publisherAgg != null && publisherAgg.size() > 0}">
							<form action="/returnPublisherBooks" method="post">
								<!-- a list without bullets -->
								<ul style="list-style: none">
									<c:forEach var="agg" items="${publisherAgg}">
										<li><input type="submit" class="btn btn-default"
											id="publisher" name="publisher" value="${agg.getKey()}">(${agg.getCount()})
										</li>
									</c:forEach>
								</ul>
							</form>
						</c:if>
						</div> <!-- card-body -->
					</div> <!-- collapseTwo -->
					</div> <!-- card -->
                  </div> <!-- accordion -->
				</div> <!-- col-md-4 -->
			<div class="col-md-8 verticalLine">
				<c:if test="${bookList != null && bookList.size() > 0}">
					<div class="table-responsive">
						<table class="table">
							<thead>
								<tr>
									<th colspan="5">Search by book ${search}: ${name} </th>
								</tr>
								<tr>
									<th>Title</th>
									<th>Author</th>
									<c:if test='${search != null && search.equals("publisher")}'>
										<th>Genre</th>
									</c:if>
									<c:if test='${search != null && search.equals("genre")}'>
										<th>Publisher</th>
									</c:if>
									<th>Year</th>
									<th>Price</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="book" items="${bookList}">
									<tr>
										<td>${book.getTitle()}</td>
										<td>${book.getAuthor()}</td>
										<c:if test='${search != null && search.equals("publisher")}'>
											<td>${book.getGenre().toString()}</td>
										</c:if>
										<c:if test='${search != null && search.equals("genre")}'>
											<td>${book.getPublisher()}</td>
										</c:if>
										<td>${book.getYear()}</td>
										<td>${book.getPrice()}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div> <!-- table-responsive -->
				</c:if>
			</div> <!-- col-md-8 -->
		</div> <!-- row -->
	</div> <!--  container -->
</body>
</html>
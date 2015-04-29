<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="row">
    <div class="col-lg-12">
        <h1 class="page-header">
            Fine
        </h1>
    </div>
</div>
<p>Ok <span class="glyphicon glyphicon-thumbs-up"></span> il file è pronto per essere scaricato!</p>
<a href="${pageContext.request.contextPath}/packager/downloadEpub?epub=${epub}" class="btn btn-primary" role="button">Scarica l'EPub</a>
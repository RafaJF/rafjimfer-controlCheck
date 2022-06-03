<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="urn:jsptagdir:/WEB-INF/tags"%>

<acme:list>
	<acme:list-column code="inventor.pelfo.list.label.code" path="code" width="30%"/>
	<acme:list-column code="inventor.pelfo.list.label.creationMoment" path="creationMoment" width="20%"/>
	<acme:list-column code="inventor.pelfo.list.label.tittle" path="name" width="20%"/>
	<acme:list-column code="inventor.pelfo.list.label.budget" path="newBudget" width="20%"/>
	<acme:list-column code="inventor.pelfo.list.label.item-code" path="item.code" width="20%"/>
	
</acme:list>

<acme:button code="inventor.item.list.button.create" action="/inventor/pelfo/create"/>


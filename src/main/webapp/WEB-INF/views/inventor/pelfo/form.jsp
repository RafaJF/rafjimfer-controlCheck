<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="urn:jsptagdir:/WEB-INF/tags"%>

<acme:form>

	<jstl:choose>
		<jstl:when test="${acme:anyOf(command, 'show, update')}">
			<acme:input-textbox code="inventor.pelfo.list.label.code" path="code" readonly="true"/>
		</jstl:when>
	</jstl:choose>
	
	<acme:input-textbox code="inventor.pelfo.list.label.creationMoment" path="creationMoment" readonly="true"/>	
	<acme:input-textbox code="inventor.pelfo.list.label.tittle" path="name"/>
	<acme:input-textarea code="inventor.pelfo.list.label.description" path="summary"/>
	<acme:input-moment code="inventor.pelfo.list.label.startperiod" path="startPeriod"/>
	<acme:input-moment code="inventor.pelfo.list.label.endperiod" path="endPeriod"/>
	<acme:input-textbox code="inventor.pelfo.list.label.budget" path="ration"/>
	
	<jstl:choose>
		<jstl:when test="${command == 'show' && newBudget.getCurrency()!=budget.getCurrency()}">
	    	<acme:input-money code="inventor.patronage.form.label.budget-conversion" path="newBudget" readonly="true"/>
	    </jstl:when>
    </jstl:choose>
	
	<acme:input-url code="inventor.pelfo.list.label.link" path="additionalInfo"/>
	
	<jstl:choose>
	
		<jstl:when test="${acme:anyOf(command, 'show, update, delete')}">
			<acme:submit code="inventor.item.form.button.update" action="/inventor/pelfo/update"/>
			<acme:submit code="inventor.item.form.button.delete" action="/inventor/pelfo/delete"/>
		</jstl:when>
		
		<jstl:when test="${command == 'create'}">
		
			<acme:input-select code="inventor.pelfo.list.label.itemsPublished" path="itemCode">
				<jstl:forEach items="${toolsByInventorPublished}" var ="item">
					<acme:input-option code="${item.code}" value="${item.code}"/>
				</jstl:forEach>
			</acme:input-select>
		
			<acme:submit code="inventor.item.list.button.create" action="/inventor/pelfo/create"/>
		</jstl:when>
	</jstl:choose>
	
</acme:form>
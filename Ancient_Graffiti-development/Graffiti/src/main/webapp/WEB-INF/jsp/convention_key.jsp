<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

	<div id="epigraphic_conventions" style="text-align: center; display: none;">
		<a
			href="http://ancientgraffiti.org/about/main/epigraphic-conventions/"
			id="ep_con_link">Epigraphic Convention Key</a>
		<table class="table-bordered" id="convention_table">
			<thead>
				<tr>
					<th>Symbol</th>
					<th>Meaning</th>
				</tr>
			</thead>
			<tbody>
				<c:if test="${fn:contains(notations, 'oncePres')}">
					<tr>
						<td id="sym">[abc]</td>
						<td id="def">Letters once present, now missing due to
							damage to the surface or support</td>
					</tr>
				</c:if>
				<c:if test="${fn:contains(notations, 'lostContent')}">
					<tr>
						<td id="sym">[- - -]</td>
						<td id="def">Damage to the surface or support; letters
							cannot be restored with certainty</td>
					</tr>
				</c:if>
				<c:if test="${fn:contains(notations, 'abbr')}">
					<tr>
						<td id="sym">a(bc)</td>
						<td id="def">Abbreviation; text was never written out;
							expanded by editor</td>
					</tr>
				</c:if>
				<c:if test="${fn:contains(notations, 'intErased')}">
					<tr>
						<td id="sym">[[abc]]</td>
						<td id="def">Letters intentionally erased in antiquity</td>
					</tr>
				</c:if>
				<c:if test="${fn:contains(notations, 'upper')}">
					<tr>
						<td id="sym">ABC</td>
						<td id="def">Letters whose reading is clear but meaning
							is incomprehensible</td>
					</tr>
				</c:if>
				<c:if test="${fn:contains(notations, 'dots')}">
					<tr>
						<td id="sym">a&#803;b&#803;</td>
						<td id="def">Characters damaged or unclear that would be
							unintelligible without context</td>
					</tr>
				</c:if>
				<c:if test="${fn:contains(notations, 'underline')}">
					<tr>
						<td id="sym"><span style="text-decoration: underline">abc</span></td>
						<td id="def">Characters formerly visible, now missing</td>
					</tr>
				</c:if>
				<c:if test="${fn:contains(notations, 'fig')}">
					<tr>
						<td id="sym">((:abc))</td>
						<td id="def">Description of a figural graffito (used by
							EDR and AGP)</td>
					</tr>
				</c:if>
				<c:if test="${fn:contains(notations, 'nonStandSpell')}">
					<tr>
						<td id="sym">(:abc)</td>
						<td id="def">Gives standard spelling to explain
							non-standard text in an inscription (used by EDR and AGP)</td>
					</tr>
				</c:if>
				<c:if test="${fn:contains(notations, 'lig')}">
					<tr>
						<td id="sym">âbc</td>
						<td id="def">Letters joined in ligature, each letter that
							is joined to the next letter is indicated by a caret</td>
					</tr>
				</c:if>
				<c:if test="${fn:contains(notations, 'illegChar')}">
					<tr>
						<td id="sym">+</td>
						<td id="def">Illegible/unclear character</td>
					</tr>
				</c:if>
				<c:if test="${fn:contains(notations, 'lostLines')}">
					<tr>
						<td id="sym">– – – – – –</td>
						<td id="def">Lost lines, quantity unknown</td>
					</tr>
				</c:if>
				<c:if test="${fn:contains(notations, 'uncert')}">
					<tr>
						<td id="sym">?</td>
						<td id="def">Represents uncertainty</td>
					</tr>
				</c:if>
				<c:if test="${fn:contains(notations, 'sym')}">
					<tr>
						<td id="sym">((abc))</td>
						<td id="def">Represents a symbol</td>
					</tr>
				</c:if>
				<c:if test="${fn:contains(notations, 'markup')}">
					<tr>
						<td id="sym">&#12296;:abc&#12297;</td>
						<td id="def">Explanation of editor, either <a
							href="https://epidoc.stoa.org/gl/latest/trans-subaudible.html">subaudible word</a> 
						or regarding the layout of the text, e.g.〈:col. I〉 (used by EDR and AGP)</td>
					</tr>
				</c:if>
			</tbody>
		</table>
		<a
			href="http://ancientgraffiti.org/about/main/epigraphic-conventions/"
			id="second_ep_con_link">Full List of Conventions &#10140;</a>
	</div>
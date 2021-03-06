package edu.uiowa.icts.FederationTagLib.outboundQuery;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;


import edu.uiowa.icts.FederationTagLib.FederationTagLibTagSupport;
import edu.uiowa.icts.FederationTagLib.Sequence;

@SuppressWarnings("serial")
public class OutboundQuery extends FederationTagLibTagSupport {

	static OutboundQuery currentInstance = null;
	boolean commitNeeded = false;
	boolean newRecord = false;

	private static final Log log = LogFactory.getLog(OutboundQuery.class);

	Vector<FederationTagLibTagSupport> parentEntities = new Vector<FederationTagLibTagSupport>();

	int qid = 0;
	String queryString = null;
	Date queryDate = null;

	private String var = null;

	private OutboundQuery cachedOutboundQuery = null;

	public int doStartTag() throws JspException {
		currentInstance = this;
		try {


			OutboundQueryIterator theOutboundQueryIterator = (OutboundQueryIterator)findAncestorWithClass(this, OutboundQueryIterator.class);

			if (theOutboundQueryIterator != null) {
				qid = theOutboundQueryIterator.getQid();
			}

			if (theOutboundQueryIterator == null && qid == 0) {
				// no qid was provided - the default is to assume that it is a new OutboundQuery and to generate a new qid
				qid = Sequence.generateID();
				insertEntity();
			} else {
				// an iterator or qid was provided as an attribute - we need to load a OutboundQuery from the database
				boolean found = false;
				PreparedStatement stmt = getConnection().prepareStatement("select query_string,query_date from federation.outbound_query where qid = ?");
				stmt.setInt(1,qid);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					if (queryString == null)
						queryString = rs.getString(1);
					if (queryDate == null)
						queryDate = rs.getTimestamp(2);
					found = true;
				}
				stmt.close();

				if (!found) {
					insertEntity();
				}
			}
		} catch (SQLException e) {
			log.error("JDBC error retrieving qid " + qid, e);

			freeConnection();
			clearServiceState();

			Tag parent = getParent();
			if(parent != null){
				pageContext.setAttribute("tagError", true);
				pageContext.setAttribute("tagErrorException", e);
				pageContext.setAttribute("tagErrorMessage", "JDBC error retrieving qid " + qid);
				return parent.doEndTag();
			}else{
				throw new JspException("JDBC error retrieving qid " + qid,e);
			}

		} finally {
			freeConnection();
		}

		if(pageContext != null){
			OutboundQuery currentOutboundQuery = (OutboundQuery) pageContext.getAttribute("tag_outboundQuery");
			if(currentOutboundQuery != null){
				cachedOutboundQuery = currentOutboundQuery;
			}
			currentOutboundQuery = this;
			pageContext.setAttribute((var == null ? "tag_outboundQuery" : var), currentOutboundQuery);
		}

		return EVAL_PAGE;
	}

	public int doEndTag() throws JspException {
		currentInstance = null;

		if(pageContext != null){
			if(this.cachedOutboundQuery != null){
				pageContext.setAttribute((var == null ? "tag_outboundQuery" : var), this.cachedOutboundQuery);
			}else{
				pageContext.removeAttribute((var == null ? "tag_outboundQuery" : var));
				this.cachedOutboundQuery = null;
			}
		}

		try {
			Boolean error = null; // (Boolean) pageContext.getAttribute("tagError");
			if(pageContext != null){
				error = (Boolean) pageContext.getAttribute("tagError");
			}

			if(error != null && error){

				freeConnection();
				clearServiceState();

				Exception e = (Exception) pageContext.getAttribute("tagErrorException");
				String message = (String) pageContext.getAttribute("tagErrorMessage");

				Tag parent = getParent();
				if(parent != null){
					return parent.doEndTag();
				}else if(e != null && message != null){
					throw new JspException(message,e);
				}else if(parent == null){
					pageContext.removeAttribute("tagError");
					pageContext.removeAttribute("tagErrorException");
					pageContext.removeAttribute("tagErrorMessage");
				}
			}
			if (commitNeeded) {
				PreparedStatement stmt = getConnection().prepareStatement("update federation.outbound_query set query_string = ?, query_date = ? where qid = ?");
				stmt.setString(1,queryString);
				stmt.setTimestamp(2,queryDate == null ? null : new java.sql.Timestamp(queryDate.getTime()));
				stmt.setInt(3,qid);
				stmt.executeUpdate();
				stmt.close();
			}
		} catch (SQLException e) {
			log.error("Error: IOException while writing to the user", e);

			freeConnection();
			clearServiceState();

			Tag parent = getParent();
			if(parent != null){
				pageContext.setAttribute("tagError", true);
				pageContext.setAttribute("tagErrorException", e);
				pageContext.setAttribute("tagErrorMessage", "Error: IOException while writing to the user");
				return parent.doEndTag();
			}else{
				throw new JspTagException("Error: IOException while writing to the user");
			}

		} finally {
			clearServiceState();
			freeConnection();
		}
		return super.doEndTag();
	}

	public void insertEntity() throws JspException, SQLException {
		if (qid == 0) {
			qid = Sequence.generateID();
			log.debug("generating new OutboundQuery " + qid);
		}

		if (queryString == null){
			queryString = "";
		}
		PreparedStatement stmt = getConnection().prepareStatement("insert into federation.outbound_query(qid,query_string,query_date) values (?,?,?)");
		stmt.setInt(1,qid);
		stmt.setString(2,queryString);
		stmt.setTimestamp(3,queryDate == null ? null : new java.sql.Timestamp(queryDate.getTime()));
		stmt.executeUpdate();
		stmt.close();
		freeConnection();
	}

	public int getQid () {
		return qid;
	}

	public void setQid (int qid) {
		this.qid = qid;
	}

	public int getActualQid () {
		return qid;
	}

	public String getQueryString () {
		if (commitNeeded)
			return "";
		else
			return queryString;
	}

	public void setQueryString (String queryString) {
		this.queryString = queryString;
		commitNeeded = true;
	}

	public String getActualQueryString () {
		return queryString;
	}

	public Date getQueryDate () {
		return queryDate;
	}

	public void setQueryDate (Date queryDate) {
		this.queryDate = queryDate;
		commitNeeded = true;
	}

	public Date getActualQueryDate () {
		return queryDate;
	}

	public void setQueryDateToNow ( ) {
		this.queryDate = new java.util.Date();
		commitNeeded = true;
	}

	public String getVar () {
		return var;
	}

	public void setVar (String var) {
		this.var = var;
	}

	public String getActualVar () {
		return var;
	}

	public static Integer qidValue() throws JspException {
		try {
			return currentInstance.getQid();
		} catch (Exception e) {
			 throw new JspTagException("Error in tag function qidValue()");
		}
	}

	public static String queryStringValue() throws JspException {
		try {
			return currentInstance.getQueryString();
		} catch (Exception e) {
			 throw new JspTagException("Error in tag function queryStringValue()");
		}
	}

	public static Date queryDateValue() throws JspException {
		try {
			return currentInstance.getQueryDate();
		} catch (Exception e) {
			 throw new JspTagException("Error in tag function queryDateValue()");
		}
	}

	private void clearServiceState () {
		qid = 0;
		queryString = null;
		queryDate = null;
		newRecord = false;
		commitNeeded = false;
		parentEntities = new Vector<FederationTagLibTagSupport>();
		this.var = null;

	}

}

package edu.uiowa.icts.FederationTagLib.response;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.uiowa.icts.FederationTagLib.FederationTagLibTagSupport;

@SuppressWarnings("serial")
public class ResponseResponseDate extends FederationTagLibTagSupport {

	String type = "DATE";
	String dateStyle = "DEFAULT";
	String timeStyle = "DEFAULT";
	String pattern = null;
	private static final Log log = LogFactory.getLog(ResponseResponseDate.class);

	public int doStartTag() throws JspException {
		try {
			Response theResponse = (Response)findAncestorWithClass(this, Response.class);
			if (!theResponse.commitNeeded) {
				String resultString = null;
				if (theResponse.getResponseDate() == null) {
					resultString = "";
				} else {
					if (pattern != null) {
						resultString = (new SimpleDateFormat(pattern)).format(theResponse.getResponseDate());
					} else if (type.equals("BOTH")) {
						resultString = DateFormat.getDateTimeInstance(formatConvert(dateStyle),formatConvert(timeStyle)).format(theResponse.getResponseDate());
					} else if (type.equals("TIME")) {
						resultString = DateFormat.getTimeInstance(formatConvert(timeStyle)).format(theResponse.getResponseDate());
					} else { // date
						resultString = DateFormat.getDateInstance(formatConvert(dateStyle)).format(theResponse.getResponseDate());
					}
				}
				pageContext.getOut().print(resultString);
			}
		} catch (Exception e) {
			log.error("Can't find enclosing Response for responseDate tag ", e);
			freeConnection();
			Tag parent = getParent();
			if(parent != null){
				pageContext.setAttribute("tagError", true);
				pageContext.setAttribute("tagErrorException", e);
				pageContext.setAttribute("tagErrorMessage", "Error: Can't find enclosing Response for responseDate tag ");
				return parent.doEndTag();
			}else{
				throw new JspTagException("Error: Can't find enclosing Response for responseDate tag ");
			}

		}
		return SKIP_BODY;
	}

	public Date getResponseDate() throws JspException {
		try {
			Response theResponse = (Response)findAncestorWithClass(this, Response.class);
			return theResponse.getResponseDate();
		} catch (Exception e) {
			log.error("Can't find enclosing Response for responseDate tag ", e);
			freeConnection();
			Tag parent = getParent();
			if(parent != null){
				pageContext.setAttribute("tagError", true);
				pageContext.setAttribute("tagErrorException", e);
				pageContext.setAttribute("tagErrorMessage", "Error: Can't find enclosing Response for responseDate tag ");
				parent.doEndTag();
				return null;
			}else{
				throw new JspTagException("Error: Can't find enclosing Response for responseDate tag ");
			}
		}
	}

	public void setResponseDate(Date responseDate) throws JspException {
		try {
			Response theResponse = (Response)findAncestorWithClass(this, Response.class);
			theResponse.setResponseDate(responseDate);
		} catch (Exception e) {
			log.error("Can't find enclosing Response for responseDate tag ", e);
			freeConnection();
			Tag parent = getParent();
			if(parent != null){
				pageContext.setAttribute("tagError", true);
				pageContext.setAttribute("tagErrorException", e);
				pageContext.setAttribute("tagErrorMessage", "Error: Can't find enclosing Response for responseDate tag ");
				parent.doEndTag();
			}else{
				throw new JspTagException("Error: Can't find enclosing Response for responseDate tag ");
			}
		}
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type.toUpperCase();
	}

	public String getDateStyle() {
		return dateStyle;
	}

	public void setDateStyle(String dateStyle) {
		this.dateStyle = dateStyle.toUpperCase();
	}

	public String getTimeStyle() {
		return timeStyle;
	}

	public void setTimeStyle(String timeStyle) {
		this.timeStyle = timeStyle.toUpperCase();
	}

	public static int formatConvert(String stringValue) {
		if (stringValue.equals("SHORT"))
			return DateFormat.SHORT;
		if (stringValue.equals("MEDIUM"))
			return DateFormat.MEDIUM;
		if (stringValue.equals("LONG"))
			return DateFormat.LONG;
		if (stringValue.equals("FULL"))
			return DateFormat.FULL;
		return DateFormat.DEFAULT;
	}

}

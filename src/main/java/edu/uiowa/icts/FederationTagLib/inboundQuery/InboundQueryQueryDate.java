package edu.uiowa.icts.FederationTagLib.inboundQuery;

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
public class InboundQueryQueryDate extends FederationTagLibTagSupport {

	String type = "DATE";
	String dateStyle = "DEFAULT";
	String timeStyle = "DEFAULT";
	String pattern = null;
	private static final Log log = LogFactory.getLog(InboundQueryQueryDate.class);

	public int doStartTag() throws JspException {
		try {
			InboundQuery theInboundQuery = (InboundQuery)findAncestorWithClass(this, InboundQuery.class);
			if (!theInboundQuery.commitNeeded) {
				String resultString = null;
				if (theInboundQuery.getQueryDate() == null) {
					resultString = "";
				} else {
					if (pattern != null) {
						resultString = (new SimpleDateFormat(pattern)).format(theInboundQuery.getQueryDate());
					} else if (type.equals("BOTH")) {
						resultString = DateFormat.getDateTimeInstance(formatConvert(dateStyle),formatConvert(timeStyle)).format(theInboundQuery.getQueryDate());
					} else if (type.equals("TIME")) {
						resultString = DateFormat.getTimeInstance(formatConvert(timeStyle)).format(theInboundQuery.getQueryDate());
					} else { // date
						resultString = DateFormat.getDateInstance(formatConvert(dateStyle)).format(theInboundQuery.getQueryDate());
					}
				}
				pageContext.getOut().print(resultString);
			}
		} catch (Exception e) {
			log.error("Can't find enclosing InboundQuery for queryDate tag ", e);
			freeConnection();
			Tag parent = getParent();
			if(parent != null){
				pageContext.setAttribute("tagError", true);
				pageContext.setAttribute("tagErrorException", e);
				pageContext.setAttribute("tagErrorMessage", "Error: Can't find enclosing InboundQuery for queryDate tag ");
				return parent.doEndTag();
			}else{
				throw new JspTagException("Error: Can't find enclosing InboundQuery for queryDate tag ");
			}

		}
		return SKIP_BODY;
	}

	public Date getQueryDate() throws JspException {
		try {
			InboundQuery theInboundQuery = (InboundQuery)findAncestorWithClass(this, InboundQuery.class);
			return theInboundQuery.getQueryDate();
		} catch (Exception e) {
			log.error("Can't find enclosing InboundQuery for queryDate tag ", e);
			freeConnection();
			Tag parent = getParent();
			if(parent != null){
				pageContext.setAttribute("tagError", true);
				pageContext.setAttribute("tagErrorException", e);
				pageContext.setAttribute("tagErrorMessage", "Error: Can't find enclosing InboundQuery for queryDate tag ");
				parent.doEndTag();
				return null;
			}else{
				throw new JspTagException("Error: Can't find enclosing InboundQuery for queryDate tag ");
			}
		}
	}

	public void setQueryDate(Date queryDate) throws JspException {
		try {
			InboundQuery theInboundQuery = (InboundQuery)findAncestorWithClass(this, InboundQuery.class);
			theInboundQuery.setQueryDate(queryDate);
		} catch (Exception e) {
			log.error("Can't find enclosing InboundQuery for queryDate tag ", e);
			freeConnection();
			Tag parent = getParent();
			if(parent != null){
				pageContext.setAttribute("tagError", true);
				pageContext.setAttribute("tagErrorException", e);
				pageContext.setAttribute("tagErrorMessage", "Error: Can't find enclosing InboundQuery for queryDate tag ");
				parent.doEndTag();
			}else{
				throw new JspTagException("Error: Can't find enclosing InboundQuery for queryDate tag ");
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

package com.himanreddy.clipit1;

public class CopyText

{
	
	private String label;
	
	private String text;
	
	private int id;
	
	public CopyText()
	
	{
		
	}
	
	public CopyText(String label,String text)
	
	{
	
		this.label = label;
		
		this.text = text;
	}
	
	public CopyText(int id,String label,String text)
	
	{
		this.id=id;
	
		this.label = label;
		
		this.text = text;
	}
	
	public int getId()
	
	{
		
		return id;
		
	}
	
	public void setId(int id)
	
	{
	
		this.id=id;
	
	}

	public String getLabel()
	
	{
		
		return label;
		
	}
	
	public void setLabel(String label)
	
	{
		
		this.label = label;
		
	}

	public String getText()
	
	{
		
		return text;
		
	}

	public void setText(String text)
	
	{
		
		this.text = text;
		
	}	

}

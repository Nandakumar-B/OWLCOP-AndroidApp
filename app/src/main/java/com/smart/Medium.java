package com.smart;

public class Medium
{
	public int limit;
	public int vacancy;
	public int inside;
	
	public Medium(){
		
	}
	public Medium(int limit,int vacancy,int inside){
		this.limit=limit;
		this.vacancy=vacancy;
		this.inside=inside;
	}
	public void setLimit(int limit){
		this.limit=limit;
	}
	public int getLimit(){
		return limit;
	}
	public void setVacancy(int vacancy){
		this.vacancy=vacancy;
	}
	public int getVacancy(){
		return vacancy;
	}
	public void setInside(int inside){
		this.inside=inside;
	}
	public int getInside(){
		return inside;
	}
}

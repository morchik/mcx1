package com.mcxsuregain.utils;

public class ServiceData {

	// URL
	public static String ip = "http://45.34.14.203:7979";
//	 public static String ip = "http://208.78.220.102:7979";
	// NAME SPACE
	public static String NAMESPACE = "http://tempuri.org/";
	// SREVICE URL
	public static String URL = ip + "/Service.asmx?WSDL".trim();

	// ACTIONS
	public static String ActionLogin = NAMESPACE + "LoginMobileUser";
	public static String ActionSymbols = NAMESPACE + "GetSymbols";
	public static String ActionMarket = NAMESPACE + "SearchMarketUpdatesMobile";
	public static String ActionGetinfo = NAMESPACE + "GetInfo";
	public static String ActionGetStates = NAMESPACE + "GetStates";
	public static String ActionRegisteration = NAMESPACE + "RegisterMobileUser";
	public static String ActionPass = NAMESPACE + "ChangePwd";

	// METHOD
	public static String MethodLogin = "LoginMobileUser";
	public static String MethodSymbols = "GetSymbols";
	public static String MethodMarket = "SearchMarketUpdatesMobile";
	public static String MethodGetInfo = "GetInfo";
	public static String MethodGetStates = "GetStates";
	public static String MethodRegisteration = "RegisterMobileUser";
	public static String MethodPass = "ChangePwd";

}

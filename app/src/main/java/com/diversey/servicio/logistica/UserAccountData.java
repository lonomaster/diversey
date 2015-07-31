package com.diversey.servicio.logistica;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

public class UserAccountData{

	private AccountManager accountManager;
	public UserAccountData(Context ctx) {
		String possibleEmail = "";
		accountManager = AccountManager.get(ctx);
		Account[] accounts = accountManager.getAccounts();
		for (Account account : accounts) {
		    if (account.type.compareTo("com.google") == 0)
		    {
		        possibleEmail = account.name;
		    }             
		}
		Log.i("Account:",possibleEmail);
	}

}

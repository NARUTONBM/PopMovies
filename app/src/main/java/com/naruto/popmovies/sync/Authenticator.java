package com.naruto.popmovies.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Android Studio.Date: 2017/10/30. Time: 下午11:14. Desc:
 * UdaCity_PopularMovies
 *
 * @author: jellybean.
 */
public class Authenticator extends AbstractAccountAuthenticator {

	public Authenticator(Context context) {
		super(context);
	}

	// Editing properties is not supported
	@Override
	public Bundle editProperties(AccountAuthenticatorResponse r, String s) {
		throw new UnsupportedOperationException();
	}

	// Don't add additional accounts
	@Override
    public Bundle addAccount(AccountAuthenticatorResponse r, String s, String s2, String[] strings, Bundle bundle) {
		return null;
	}

	// Ignore attempts to confirm credentials
	@Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse r, Account account, Bundle bundle) {
		return null;
	}

	// Getting an authentication token is not supported
	@Override
    public Bundle getAuthToken(AccountAuthenticatorResponse r, Account account, String s, Bundle bundle) {
		throw new UnsupportedOperationException();
	}

	// Getting a label for the auth token is not supported
	@Override
	public String getAuthTokenLabel(String s) {
		throw new UnsupportedOperationException();
	}

	// Updating user credentials is not supported
	@Override
    public Bundle updateCredentials(AccountAuthenticatorResponse r, Account account, String s, Bundle bundle) {
		throw new UnsupportedOperationException();
	}

	// Checking features for the account is not supported
	@Override
    public Bundle hasFeatures(AccountAuthenticatorResponse r, Account account, String[] strings) {
		throw new UnsupportedOperationException();
	}
}
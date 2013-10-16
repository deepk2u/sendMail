package com.deepak.test;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class SendMailTrustManager implements X509TrustManager {

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    public boolean isClientTrusted(X509Certificate[] arg0) {
        return true;
    }

    public boolean isServerTrusted(X509Certificate[] arg0) {
        return true;
    }

    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // TODO Auto-generated method stub

    }

    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // TODO Auto-generated method stub

    }

}
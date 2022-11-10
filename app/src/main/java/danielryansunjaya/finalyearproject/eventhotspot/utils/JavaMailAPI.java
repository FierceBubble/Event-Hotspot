package danielryansunjaya.finalyearproject.eventhotspot.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailAPI extends AsyncTask<Void,Void,Void>  {

    private static final String TAG ="JavaMailAPI";
    private Context mContext;
    private Session mSession;

    private String mEmail;
    private String mSubject;
    private String mMessage;

    private ProgressDialog mProgressDialog;

    public JavaMailAPI(Context mContext, String mEmail, String mSubject, String mMessage) {
        this.mContext = mContext;
        this.mEmail = mEmail;
        this.mSubject = mSubject;
        this.mMessage = mMessage;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Show progress dialog while sending email
        mProgressDialog = ProgressDialog.show(mContext,"Sending message", "Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismiss progress dialog when message successfully send
        mProgressDialog.dismiss();

        //Show success toast
        Log.i(TAG, "Message Sent");
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        mSession = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EmailUtils.EMAIL, EmailUtils.PASSWORD);
                    }
                });

        try {
            //Creating Message
            /*InternetAddress[] cc = new InternetAddress[3];
            cc[0] = new InternetAddress("diamondryancraft@gmail.com");
            cc[1] = new InternetAddress("diamondryan.ml@gmail.com");
            cc[2] = new InternetAddress("diamondryan.cn@gmail.com");
            */
            MimeMessage mm = new MimeMessage(mSession);
            mm.setFrom(new InternetAddress(EmailUtils.EMAIL, "UCSI Event Hotspot"));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(mEmail));
            //mm.addRecipients(Message.RecipientType.CC, cc);
            mm.setSubject(mSubject);
            mm.setText(mMessage);
            Transport.send(mm);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
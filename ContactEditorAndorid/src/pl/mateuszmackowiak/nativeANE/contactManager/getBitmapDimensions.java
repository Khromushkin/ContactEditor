package pl.mateuszmackowiak.nativeANE.contactManager;

import java.io.InputStream;

import android.content.ContentUris;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

public class getBitmapDimensions implements FREFunction {
	
	public static final String KEY ="getBitmapDimensions";
			
	@Override
	public FREObject call(FREContext context, FREObject[] arg1) {
		int recordId;
		Boolean isThumbnail;
		try {
			recordId = arg1[0].getAsInt();
		} catch (Exception e) {
			Log.e(KEY, "no incoming recordId");
			return null;
		}
		try {
			isThumbnail = arg1[1].getAsBool();
		} catch (Exception e) {
			isThumbnail = false;
		}
		
		try {
			Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, recordId);
			InputStream photoStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getActivity().getContentResolver(), contactUri, !isThumbnail);
			if(photoStream == null) {
				return null;
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(photoStream,null,options);
			FREObject resultPoint = FREObject.newObject("flash.geom.Point", null);
			resultPoint.setProperty("x", FREObject.newObject(options.outWidth));
			resultPoint.setProperty("y", FREObject.newObject(options.outHeight));
			return resultPoint;
		} catch (Exception e) {
			Log.e(KEY, e.getMessage());
			return null;
		}
		
	}

}

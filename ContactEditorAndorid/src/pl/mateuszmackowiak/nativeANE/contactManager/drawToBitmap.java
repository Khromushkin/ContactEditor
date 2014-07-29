package pl.mateuszmackowiak.nativeANE.contactManager;

import java.io.InputStream;
import java.nio.ByteBuffer;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

import com.adobe.fre.FREBitmapData;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

public class drawToBitmap implements FREFunction {
	
	public static final String KEY ="drawToBitmap";
		
	@Override
	public FREObject call(FREContext context, FREObject[] arg1) {
		FREBitmapData bitmapData;
		int recordId;
		Boolean isThumbnail;
		try {
			bitmapData = (FREBitmapData)arg1[0];
		} catch (Exception e) {
			Log.e(KEY, "no incoming bitmapdata");
			return null;
		}
		
		try {
			recordId = arg1[1].getAsInt();
		} catch (Exception e) {
			Log.e(KEY, "no incoming recordId");
			return null;
		}
		try {
			isThumbnail = arg1[2].getAsBool();
		} catch (Exception e) {
			isThumbnail = false;
		}
		
		try {
			Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, recordId);
			InputStream photoStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getActivity().getContentResolver(), contactUri, !isThumbnail);
			if(photoStream == null) {
				return null;
			}
						
			Bitmap bitmap = BitmapFactory.decodeStream(photoStream);	
			Bitmap rgbaBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
			Paint m_paint = new Paint();   
			float[] m_bgrToRgbColorTransform  =
			    {
			        0,  0,  1f, 0,  0, 
			        0,  1f, 0,  0,  0,
			        1f, 0,  0,  0,  0, 
			        0,  0,  0,  1f, 0
			    };
			ColorMatrix m_colorMatrix = new ColorMatrix(m_bgrToRgbColorTransform);
			ColorMatrixColorFilter m_colorFilter = new ColorMatrixColorFilter(m_colorMatrix);

			Canvas m_canvas = new Canvas(rgbaBitmap);
			m_paint.setColorFilter(m_colorFilter);

			//
			// Convert the bitmap from BGRA to RGBA.
			//
			m_canvas.drawBitmap(bitmap, 0, 0, m_paint);
			
			bitmapData.acquire();
			ByteBuffer bitmapDataBytes = bitmapData.getBits();
			rgbaBitmap.copyPixelsToBuffer(bitmapDataBytes);
			bitmapData.invalidateRect(0, 0, bitmapData.getWidth(), bitmapData.getHeight());
			bitmapData.release();
			return bitmapData;
		} catch (Exception e) {
			Log.e(KEY, e.getMessage());
			return null;
		}
		
	}

}

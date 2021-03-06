/**
 *  ReGalAndroid, a gallery client for Android, supporting G2, G3, etc...
 *  URLs: https://github.com/anthonydahanne/ReGalAndroid , http://blog.dahanne.net
 *  Copyright (c) 2010 Anthony Dahanne
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package net.dahanne.android.regalandroid.tasks;

import java.io.File;

import net.dahanne.android.regalandroid.activity.Settings;
import net.dahanne.android.regalandroid.remote.RemoteGalleryConnectionFactory;
import net.dahanne.android.regalandroid.utils.AndroidUriUtils;
import net.dahanne.android.regalandroid.utils.ShowUtils;
import net.dahanne.gallery.commons.remote.GalleryConnectionException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;

public class AddPhotoTask extends AsyncTask<Object, Void, String> {
	private String exceptionMessage = null;
	Activity activity;
	private String galleryUrl;
	private final ProgressDialog progressDialog;

	public AddPhotoTask(Activity context, ProgressDialog progressDialog) {
		super();
		activity = context;
		this.progressDialog = progressDialog;
	}

	@Override
	protected String doInBackground(Object... parameters) {
		String galleryUrl = (String) parameters[0];
		Integer albumName = (Integer) parameters[1];
		Uri photoUri = (Uri) parameters[2];
		boolean mustLogIn = (Boolean) parameters[3];
		String imageName = (String) parameters[4];
		File imageFile = (File) parameters[5];

		// not from the camera
		if (imageFile == null) {
			imageFile = AndroidUriUtils.getFileFromUri(photoUri, activity);
		}

		try {
			if (mustLogIn) {
				RemoteGalleryConnectionFactory.getInstance().loginToGallery();
			}
			RemoteGalleryConnectionFactory.getInstance().uploadPictureToGallery(galleryUrl, albumName, imageFile,
					imageName, Settings.getDefaultSummary(activity),
					Settings.getDefaultDescription(activity));
		} catch (GalleryConnectionException e) {
			exceptionMessage = e.getMessage();
		}
		return imageName;
	}

	@Override
	protected void onPostExecute(String imageName) {

		progressDialog.dismiss();
		if (exceptionMessage != null) {
			// Something went wrong
			ShowUtils.getInstance().alertConnectionProblem(exceptionMessage,
					galleryUrl, activity);
		} else {
			ShowUtils.getInstance().toastImageSuccessfullyAdded(activity,
					imageName);
		}

	}

}

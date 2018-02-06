package com.premium.patternbox.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.premium.patternbox.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppConfig {
	// Server API url
	public static String API_URL = "http://patternbox.us/api/v2/";
	public static String GOOGLE_SEARCH_URL = "https://www.google.com/search?q=";
 	public static String GOOGLE_DRIVE_URL = "http://drive.google.com/viewerng/viewer?embedded=true&url=";
	public static  String API_TAG_LOGIN = "login";
	public static  String API_TAG_REGISTER = "register";
	public static  String API_TAG_RESET = "reset";
	public static  String API_TAG_INIT = "init";
	public static  String API_TAG_PATTERNS="patterns";
	public static  String API_TAG_UPLOAD="upload";
    public static  String API_TAG_CATEGORIES = "categories";
    public static  String API_TAG_BOOKMARK = "bookmark";
	public static String API_TAG_UPDATE_IMAGE = "updateimage";

	public static  String API_TAG_FABRIC_CATEGORIES = "fabric_categories";
	public static  String API_TAG_FABRIC_BOOKMARK = "fabric_bookmark";
	public static  String API_TAG_FABRICS_UPLOAD="upload_fabric";
	public static  String API_TAG_FABRICS="fabrics";

	public static  String API_TAG_NOTION_CATEGORIES = "notion_categories";
	public static  String API_TAG_NOTION = "notion";

	public static  String API_TAG_PROJECTS = "projects";
	public static  String API_TAG_PROJECT_UPLOAD = "upload_project";

	public static final int REQUEST_MEDIA = 3001;

	//Pattern Parameter
	public static  String KPatternId = "id";
	public static String KPatternIsPDF       = "is_pdf";
	public static String KPatternName        ="name";
	public static String KPatternKey         ="pattern_key";
	public static String KPatternCategories  ="categories";   //categories array
	public static String KPatternInfo        ="info";
	public static String KPatternFronPic     ="front_image_name";
	public static String KPatternBackPic     ="back_image_name";
	public static String KPatternBookMark    ="is_bookmark";
	public static String KPatternUrls        ="pdf_urls";
	public static String KPatternUserId      ="user_id";

    public static String KCategoryId = "id";
    public static String KCategoryName = "category";
    public static String KCategoryIcon = "icon";

	//Fabric Parameters
	public static String KFabricId			="id";
	public static String KFabricImageName	="image";
	public static String KFabricCategories	="categories";
	public static String KFabricBookMark	="is_bookmark";

	public static String KFabricName		="name";
	public static String KFabricType		="type";
	public static String KFabricColor		="color";
	public static String KFabricWidth		="width";
	public static String KFabricLength		="length";
	public static String KFabricWeight		="weight";
	public static String KFabricStretch		="stretch";
	public static String KFabricUses		="uses";
	public static String KFabricCareInstructions	="care_instructions";
	public static String KFabricNotes		="notes";

	//Notion Parameters
	public static String KNotionId			="id";
	public static String KNotionCategoryId  ="category_id";
	public static String KNotionType        ="type";
	public static String KNotionSize        ="size";
	public static String KNotionColor       ="color";

	//Project Parameters
	public static String KProjectId     	    ="id";
	public static String KProjectName	      	="name";
	public static String KProjectClient     	="client";
	public static String KProjectMeasurement 	="measurement";
	public static String KProjectImageName   	="image";
	public static String KProjectPattern     	="pattern";
	public static String KProjectNotes       	="notes";
	public static String KProjectFabrics     	="fabrics";
	public static String KProjectNotions     	="notions";

	public static int isPdf = 1;

	public static boolean isSaved = false;
	public static File front_img_file = null;
	public static File back_img_file = null;

	public static boolean isUpdate = false;
	public static int isFirstOpen = 0;
	public static boolean isClose = false;

	// Public Variable
	public static ArrayList<PatternCategoryInfo> patternCategoryList;
	public static ArrayList<FabricCategoryInfo> fabricCategoryList;
	public static ArrayList<NotionCategoryInfo> notionCategoryList;

	public static ArrayList<PatternInfo> patternList;
	public static ArrayList<FabricInfo> fabricList;
	public static ArrayList<NotionInfo> notionList;
	public static ArrayList<ProjectInfo> projectList;
	public static ArrayList<NotionEditInfo> notionEditInfoList;
	public static ArrayList<String> historyList;
	public static ArrayList<String> pdfList;

	public static PatternInfo selPatternInfo;
	public static FabricInfo selFabricInfo;
	public static ProjectInfo selProjectInfo;

	public static int selPatternID;
	public static String selFabricIDs;
	public static String selNotionIDs;
	public static String selNotionIDsFrom;

	public static void initData() {
		patternCategoryList = new ArrayList<PatternCategoryInfo>();
		fabricCategoryList = new ArrayList<FabricCategoryInfo>();
		notionCategoryList = new ArrayList<NotionCategoryInfo>();
		historyList = new ArrayList<String>();

		patternList = new ArrayList<PatternInfo>();
		fabricList = new ArrayList<FabricInfo>();
		notionList = new ArrayList<NotionInfo>();
		projectList = new ArrayList<ProjectInfo>();

		PatternCategoryInfo c1 = new PatternCategoryInfo("d0", "Tops", R.drawable.c_top, true);
		patternCategoryList.add(c1);
		PatternCategoryInfo c2 = new PatternCategoryInfo("d1", "Pants", R.drawable.c_pant, true);
		patternCategoryList.add(c2);
		PatternCategoryInfo c3 = new PatternCategoryInfo("d2", "Dresses", R.drawable.c_dress, true);
		patternCategoryList.add(c3);
		PatternCategoryInfo c4 = new PatternCategoryInfo("d3", "Skirts", R.drawable.c_skirt, true);
		patternCategoryList.add(c4);
		PatternCategoryInfo c5 = new PatternCategoryInfo("d4", "Outerwear", R.drawable.c_outerwear, true);
		patternCategoryList.add(c5);
		PatternCategoryInfo c6 = new PatternCategoryInfo("d5", "Knits", R.drawable.c_knit, true);
		patternCategoryList.add(c6);
		PatternCategoryInfo c7 = new PatternCategoryInfo("d6", "Accessories", R.drawable.c_accessory, true);
		patternCategoryList.add(c7);
		PatternCategoryInfo c8 = new PatternCategoryInfo("d7", "Children", R.drawable.c_children, true);
		patternCategoryList.add(c8);
		PatternCategoryInfo c9 = new PatternCategoryInfo("d8", "Home", R.drawable.c_home, true);
		patternCategoryList.add(c9);

		FabricCategoryInfo f1 = new FabricCategoryInfo("d1", "Wovens", true);
		fabricCategoryList.add(f1);
		FabricCategoryInfo f2 = new FabricCategoryInfo("d2", "Knit", true);
		fabricCategoryList.add(f2);
		FabricCategoryInfo f3 = new FabricCategoryInfo("d3", "Decor", true);
		fabricCategoryList.add(f3);

		NotionCategoryInfo n1 = new NotionCategoryInfo("d1", "Zippers", true);
		notionCategoryList.add(n1);
		NotionCategoryInfo n2 = new NotionCategoryInfo("d2", "Buttons", true);
		notionCategoryList.add(n2);
		NotionCategoryInfo n3 = new NotionCategoryInfo("d3", "Threads", true);
		notionCategoryList.add(n3);
		NotionCategoryInfo n4 = new NotionCategoryInfo("d4", "Needles", true);
		notionCategoryList.add(n4);
	}
	// add pattern category
	public static boolean addPatternCategory(PatternCategoryInfo info) {
		PatternCategoryInfo c1 = new PatternCategoryInfo(info.id, info.name, info.icon, info.isDefault);
		patternCategoryList.add(c1);

		return true;
	}
	//add fabric category
	public static boolean addFabricCategory(FabricCategoryInfo info) {
		FabricCategoryInfo c1 = new FabricCategoryInfo(info.id, info.name, info.isDefault);
		fabricCategoryList.add(c1);

		return true;
	}
	//add notion category
	public static boolean addNotionCategory(NotionCategoryInfo info) {
		NotionCategoryInfo c1 = new NotionCategoryInfo(info.id, info.name, info.isDefault);
		notionCategoryList.add(c1);

		return true;
	}
	//get pattern by id
	public static PatternInfo getPatternbyID(int patternID) {
		for (int i = 0; i < AppConfig.patternList.size(); i ++) {
			PatternInfo temp = AppConfig.patternList.get(i);
			if (temp.id == patternID) return temp;
		}
		return null;
	}
	//get fabric by id
	public static FabricInfo getFabricbyID(int fabricID) {
		for (int i = 0; i < AppConfig.fabricList.size(); i ++) {
			FabricInfo temp = AppConfig.fabricList.get(i);
			if (temp.id == fabricID) return temp;
		}
		return null;
	}
	//add pattern
	public static boolean addPattern(PatternInfo info) {
        PatternInfo c1 = new PatternInfo();
        c1.categories = info.categories;
        c1.key = info.key;
        c1.info = info.info;
        c1.name = info.name;
        c1.isPdf = info.isPdf;
        c1.isBookmark = info.isBookmark;
        c1.frontImage = info.frontImage;
        c1.backImage = info.backImage;
        c1.id = info.id;
		c1.urls = info.urls;
        patternList.add(c1);

        return true;
    }
	//add fabric
    public static boolean addFabric(FabricInfo info) {
		FabricInfo newItem = new FabricInfo();
		newItem.id = info.id;
		newItem.imageUrl = info.imageUrl;
		newItem.isBookmark = info.isBookmark;
		newItem.categories = info.categories;

		newItem.name = info.name;
		newItem.type = info.type;
		newItem.color = info.color;
		newItem.width = info.width;
		newItem.length = info.length;
		newItem.weight = info.weight;
		newItem.stretch = info.stretch;
		newItem.uses = info.uses;
		newItem.careInstructions = info.careInstructions;
		newItem.notes = info.notes;
		fabricList.add(newItem);

		return true;
	}
	//add notion
	public static boolean addNotion(NotionInfo info) {
		NotionInfo newItem = new NotionInfo();
		newItem.id = info.id;
		newItem.category = info.category;
		newItem.type = info.type;
		newItem.color = info.color;
		newItem.size = info.size;
		newItem.count = info.count;
		notionList.add(newItem);

		return true;
	}
	//add project
	public static boolean addProject(ProjectInfo info) {
		ProjectInfo newItem = new ProjectInfo();
		newItem.id = info.id;
		newItem.name = info.name;
		newItem.client = info.client;
		newItem.notes = info.notes;
		newItem.measurements = info.measurements;
		newItem.image = info.image;
		newItem.pattern = info.pattern;
		newItem.fabrics = info.fabrics;
		newItem.notions = info.notions;
		projectList.add(newItem);

		return true;
	}
	//get download url from filename
	public static String downloadUrl(int userId, String fileName){
		String url = API_URL + "../upload/";
		String filenameArray[] = fileName.split("\\.");
		String extension = filenameArray[filenameArray.length-1];
		if (extension == "pdf"){
			url = url + "pdf/"+String.valueOf(userId)+"/"+fileName;
		}else{
			url = url + "image/"+String.valueOf(userId)+"/"+fileName;
		}
		return url;
	}
	//get customized pattern categories
	public static ArrayList<PatternCategoryInfo> getCustomizedPatternCategoryList() {

		ArrayList<PatternCategoryInfo> newList = new ArrayList<PatternCategoryInfo>();
		for (int i = 0; i < patternCategoryList.size(); i ++) {
			PatternCategoryInfo info = patternCategoryList.get(i);
			if (!info.isDefault) {
				newList.add(info);
			}
		}
		return newList;
	}
	//get patterns by category (position)
	public static ArrayList<PatternInfo> getPatternsbyCategory(int position) {
		PatternCategoryInfo selInfo = AppConfig.patternCategoryList.get(position);

		ArrayList<PatternInfo> newList = new ArrayList<PatternInfo>();
		for (int i = 0; i < AppConfig.patternList.size(); i ++) {
			PatternInfo info = AppConfig.patternList.get(i);
			if (info.isPdf == isPdf) {
				String[] separated = info.categories.split(",");
				for (int j = 0; j < separated.length; j++) {
					if (separated[j].equals(selInfo.id)) {
						newList.add(info);
						break;
					}
				}
			}
		}
		return newList;
	}
	//get patterns by bookmarks
	public static ArrayList<PatternInfo> getPatternsbyBookmark() {

		ArrayList<PatternInfo> newList = new ArrayList<PatternInfo>();
		for (int i = 0; i < AppConfig.patternList.size(); i ++) {
			PatternInfo info = AppConfig.patternList.get(i);
			if (info.isBookmark > 0) {
				newList.add(info);
			}
		}
		return newList;
	}
	//get customized fabric categories
	public static ArrayList<FabricCategoryInfo> getCustomizedFabricCategoryList() {

		ArrayList<FabricCategoryInfo> newList = new ArrayList<FabricCategoryInfo>();
		for (int i = 0; i < fabricCategoryList.size(); i ++) {
			FabricCategoryInfo info = fabricCategoryList.get(i);
			if (!info.isDefault) {
				newList.add(info);
			}
		}
		return newList;
	}
	//get fabrics by category (position)
	public static ArrayList<FabricInfo> getFabricsbyCategory(int position) {
		FabricCategoryInfo selInfo = AppConfig.fabricCategoryList.get(position);

		ArrayList<FabricInfo> newList = new ArrayList<FabricInfo>();
		for (int i = 0; i < AppConfig.fabricList.size(); i ++) {
			FabricInfo info = AppConfig.fabricList.get(i);
			String[] separated = info.categories.split(",");
			for (int j = 0; j < separated.length; j++) {
				if (separated[j].equals(selInfo.id)) {
					newList.add(info);
					break;
				}
			}
		}
		return newList;
	}
	//get fabrics by bookmark
	public static ArrayList<FabricInfo> getFabricsbyBookmark() {

		ArrayList<FabricInfo> newList = new ArrayList<FabricInfo>();
		for (int i = 0; i < AppConfig.fabricList.size(); i ++) {
			FabricInfo info = AppConfig.fabricList.get(i);
			if (info.isBookmark > 0) {
				newList.add(info);
			}
		}
		return newList;
	}
	// Get notions by category
	public static ArrayList<NotionInfo> getNotionsbyCategory(String category) {

		ArrayList<NotionInfo> newList = new ArrayList<NotionInfo>();
		for (int i = 0; i < AppConfig.notionList.size(); i ++) {
			NotionInfo info = AppConfig.notionList.get(i);
			if (info.category.equals(category)) {
				newList.add(info);
			}
		}
		return newList;
	}

	//get pattern category name by id
	public static String getPatternCategoryName(String id){
		for (int i = 0; i < AppConfig.patternCategoryList.size(); i++){
			PatternCategoryInfo c = AppConfig.patternCategoryList.get(i);
			if (c.id.equals(id)){
				return c.name;
			}
		}
		return null;
	}
	//get fabric category name by id
    public static String getFabricCategoryName(String id){
        for (int i = 0; i < AppConfig.fabricCategoryList.size(); i++){
            FabricCategoryInfo c = AppConfig.fabricCategoryList.get(i);
            if (c.id.equals(id)){
                return c.name;
            }
        }
        return null;
    }

    //user defines
	public static String getStringImage(Bitmap bmp){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] imageBytes = baos.toByteArray();
		String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
		return encodedImage;
	}

	public static File savebitmap(String filename) {
		String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		OutputStream outStream = null;

		File file = new File(filename);
		if (file.exists()) {
			file.delete();
			file = new File(extStorageDirectory, filename + ".png");
			Log.e("file exist", "" + file + ",Bitmap= " + filename);
		}
		try {
			// make a new bitmap from your file
			Bitmap bitmap = BitmapFactory.decodeFile(file.getName());

			outStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream);
			outStream.flush();
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.e("file", "" + file);
		return file;
	}

	public static boolean setNotionSetting() {
		notionEditInfoList = new ArrayList<NotionEditInfo>();
		for (int k = 0; k < notionCategoryList.size(); k ++) {
			NotionCategoryInfo categoryInfo = notionCategoryList.get(k);

			ArrayList<NotionInfo> notionInfoList = new ArrayList<NotionInfo>();
			if (!selNotionIDs.equals("")) {
				String[] ids = selNotionIDs.split(":");
                for (int j = 0; j < ids.length; j++) {
                    for (int i = 0; i < notionList.size(); i++) {
                        NotionInfo notionInfo = notionList.get(i);
                        if (categoryInfo.id.equals(notionInfo.category)) {
                            String[] temp = ids[j].split(",");
                            int id = Integer.parseInt(temp[0]);
                            int count = Integer.parseInt(temp[1]);
                            if (notionInfo.id == id) {
                                NotionInfo newItem = new NotionInfo();
                                newItem.id = notionInfo.id;
                                newItem.category = notionInfo.category;
                                newItem.type = notionInfo.type;
                                newItem.color = notionInfo.color;
                                newItem.size = notionInfo.size;
                                newItem.count = count;
                                notionInfoList.add(newItem);
                                break;
                            }
                        }
                    }
				}
			}
			NotionEditInfo notionEditInfo = new NotionEditInfo(categoryInfo.id, categoryInfo.name, categoryInfo.isDefault, notionInfoList);
			notionEditInfoList.add(notionEditInfo);
		}
		return true;
	}

	public final static boolean isValidEmail(String email) {
		Pattern pattern;
		Matcher matcher;
		final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(email);
		return matcher.matches();

	}
}

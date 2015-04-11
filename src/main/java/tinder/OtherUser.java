package tinder;

import model.Photo;

import java.util.ArrayList;

public class OtherUser {
	String id;
	String gender;
	int genderNumber;
	String name;
	ArrayList<Photo> photos;
	String birthday;
    boolean matched;

	public OtherUser(String id, String gender, String name, ArrayList<Photo> photos, String birthday){
		this.id=id;
		this.gender=gender;
		if(gender.equals("female"))
			this.genderNumber=1;
		else
			this.genderNumber=0;
		this.name=name;
		this.photos=photos;
		this.birthday=birthday;
	}
	
	public OtherUser(String id, int gender, String name, ArrayList<Photo> photos, String birthday){
		this.id=id;
		this.genderNumber=gender;
		if(gender==1)
			this.gender="female";
		else
			this.gender="male";
		this.name = name;
		this.photos=photos;
		this.birthday=birthday;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("ID: ");
		sb.append(id);
		sb.append("\nName: ");
		sb.append(name);
		sb.append("\nGender");
		sb.append(gender);
		sb.append("\nPics:");
		sb.append(photos.toString());
		return sb.toString();
	}

	public String getId() {
		return id;
	}

	public String getGender() {
		return gender;
	}

	public int getGenderNumber() {
		return genderNumber;
	}

	public String getName() {
		return name;
	}

	public String getBirthday(){
		return birthday;
	}
	public ArrayList<Photo> getPhotos() {
		return photos;
	}


}

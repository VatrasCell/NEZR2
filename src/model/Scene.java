package model;

public enum Scene {
	Survey("survey_"),
	Location("location"),
	Login("login"),
	Start("start"),
	Admin("admin");
	
	private String scene;

	Scene(String scene) {
        this.scene = scene;
    }

    public String scene() {
        return scene;
    }
}

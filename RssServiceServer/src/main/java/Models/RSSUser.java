package Models;

public class RSSUser {

    private String user_type;

    protected int owner_id;

    public int getOwnerId() {
        return owner_id;
    }

    public String getUserType() {
        return user_type;
    }

    public void setUserType(String user_type) {
        this.user_type = user_type;
    }
}

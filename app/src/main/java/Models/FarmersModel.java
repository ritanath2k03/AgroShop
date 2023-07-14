package Models;

import java.util.HashMap;

public class FarmersModel {

    public static final String KEY_DISTRIBUTOR_ID = "distributorId";
    public static final String ORDERID = "OrderId";
    public static final String KEY_REMAINING_PRODUCT_QUANTITY = "remainingProductQuantity";
    public static final String ORDER_QUANTITY = "orderQuantity";
    public static final String KEY_FARMER_PHONENUMBER ="farmerPhoneNumber" ;
    public static final String KEY_DISTRIBUTOR_PHONE_NUMBER = "distributorPhoneNumber";
    public static final String KEY_DISTRIBUTOR_LOCATION = "distributorLocation";
    public static final String KEY_PRODUCT_ID ="selectedProductId" ;
    public static final String KEY_FARMER_NAME = "farmerName";
    public static final String KEY_DISTRIBUTOR_NAME = "distributorName";
    public static final String KEY_ORDER_COLLECTION = "OrderCollection";
    public static final String TOTAL_ORDER_AMOUNT ="totalAmount" ;
    String Id,Name,Location,Time,PictureUri,Phone,Email,Password,Designation;
   public  static final String KEY_COLLECTION_USER="Users";
   public  static final String KEY_DNAME="DName";
   public  static final String KEY_EMAIL="Email";
   public  static final String KEY_PAASSWORD="Password";
   public  static final String KEY_PREFERENCE_NAME="ChatAppPreferance";
   public  static final String KEY_IS_SIGNED_IN="IsSignedIn";
   public  static final String KEY_USERID="UserId";

   public  static final String KEY_IMAGE="Image";
   public  static final String KEY_PHONE_NUMBER="Phone";
   public  static final String KEY_FNAME="FName";
   public  static final String KEY_CNAME="CName";
   public  static final String KEY_PICTURE_URI="PictureUri";

   public  static final String KEY_DESIGNATION="Designation";
   public  static final String KEY_FCM="fcmToken";
   public  static final String KEY_USER="EachUser";
   public  static final String KEY_USERNAME="userName";

   public  static final String KEY_COLLECTION_CHAT="CHAT";
   public  static final String KEY_SENDER_ID="senderId";
   public  static final String KEY_RECEIVER_ID="receiverId";
   public  static final String KEY_message="message";
   public  static final String KEY_TIMESTAMP="timestamp";
   public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
   public static final String KEY_SENDER_NAME = "senderName";
   public static final String KEY_RECEIVER_NAME = "receiverName";
   public static final String KEY_SENDER_IMAGE ="senderImage";
   public static final String KEY_RECEIVER_IMAGE = "receiverImage";
   public static final String KEY_LAST_MESSAGE= "lastMessage";

   public static final String KEY_ITEM_NAME= "itemName";
   public static final String KEY_ITEM_DESCRIPTION= "itemDescription";
   public static final String KEY_ITEM_PICTURE= "itemPicture";
   public static final String KEY_ITEM_PRICE= "itemPrice";
   public static final String KEY_FARMER_LOCATION= "farmerLocation";
   public static final String KEY_ITEM_STATUS= "itemStatus";
    public static final String KEY_FARMER_ID ="FarmerId" ;
    public static final String KEY_ITEM_DATE = "ItemDate";
   public static final String KEY_MENU_COLLECTION= "MenuCollection";


   public static final String KEY_AVAILABILITY = "availability";
   public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
   public  static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
   public static final String REMOTE_MSG_DATA = "data";
   public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
   public static final String KEY_PERSON_BIO = "bio";
   public static final String KEY_PERSON_AGE = "age";
   public static final String KEY_PERSON_LOCATION = "location";



   public static HashMap<String, String> remoteMsgHeaders = null;
   public static HashMap<String, String> getRemoteMsgHeaders() {
       if (remoteMsgHeaders == null) {
           remoteMsgHeaders =new HashMap<>();
           remoteMsgHeaders.put(
                   REMOTE_MSG_AUTHORIZATION,
                   "key=AAAA8rzvZBQ:APA91bHU5GaU_1W00NY5YscsgUP7JvjDCiZaKvpWHOlrYyPuHNVuJ77A01fJxM_KpyrFC_YM67Km4tOLO-5QZzLdOWUoUK1Glk0IxCKjcIBHUyl1qBs1fat1xVRobfAxaxMMjnEQwgsI"
           );
           remoteMsgHeaders.put(
                   REMOTE_MSG_CONTENT_TYPE,
                   "application/json"
           );
       }
       return remoteMsgHeaders;



















   }









    public FarmersModel() {
    }

    public String getPictureUri() {
        return PictureUri;
    }

    public void setPictureUri(String pictureUri) {
        PictureUri = pictureUri;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }


    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }
}

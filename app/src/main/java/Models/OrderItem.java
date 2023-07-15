package Models;

import java.io.Serializable;

public class OrderItem implements Serializable {
  public   String distributorName,distributorLocation,distributorPhoneNumber,distributorProductName,distributorQuantity,distributorAmount,distributorOrder;

  public String orderStatus;
  public String currentDistributorUserId;
  public String currentFarmerUserId;
  public String currentUser;
  public String ProductName;
  public String orderId;
}

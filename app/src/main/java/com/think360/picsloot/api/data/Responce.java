package com.think360.picsloot.api.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by think360 on 09/10/17.
 */

public class Responce {

    public class SendOtpResponce{

        @SerializedName("status")
        @Expose
        private Boolean status;

        @SerializedName("message")
        @Expose
        private String message;

        @SerializedName("mobile_no")
        @Expose
        private String mobile_no;

        public Boolean getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getMobile_no() {
            return mobile_no;
        }


    }

    public class VerifyOtpResponce{

        @SerializedName("status")
        @Expose
        private boolean status;

        @SerializedName("message")
        @Expose
        private String message;

        public boolean getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

    }
    public class UploadProfilePicResponce{

        public Boolean getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getImage_url() {
            return image_url;
        }

        @SerializedName("status")
        @Expose
        private Boolean status;

        @SerializedName("message")
        @Expose
        private String message;

        @SerializedName("image_url")
        @Expose
        private String image_url;
    }
    public class RegCompleteResponce{

        @SerializedName("status")
        @Expose
        private Boolean status;

        @SerializedName("message")
        @Expose
        private String message;

        @SerializedName("data")
        @Expose
        private Data data;

        public Boolean getStatus() {return status;}
        public String getMessage() {
            return message;
        }
        public Data getData() {return data;}

        public class Data{

            public String getUser_id() {return user_id;}

            @SerializedName("user_id")
            @Expose
            private String user_id;

            @SerializedName("mobile_no")
            @Expose
            private String mobile_no;

            public String getMobile_no() {
                return mobile_no;
            }

            public String getMobile_verify() {
                return mobile_verify;
            }

            @SerializedName("mobile_verify")
            @Expose
            private String mobile_verify;

        }

    }

    public class LatestOrderResponce{

        public Boolean getStatus() {return status;}

        public LatestOrder getLatest_order() {return Latest_order;}

        @SerializedName("status")
        @Expose
        private Boolean status;

        @SerializedName("Latest_order")
        @Expose
        private LatestOrder Latest_order;

  public class LatestOrder{
         public String getDate_sent() {return date_sent;}

         public String getSent_message() {return sent_message;}

         public String getDate_recieved() {return date_recieved;}

         public String getRecieved_message() {return recieved_message;}

         public String getDate_shipped() {return date_shipped;}

         public String getShipped_message() {return shipped_message;}

         public String getDate_complete() {return date_complete;}

         public String getComplete_message() {return complete_message;}

         public List<String> getImage() {return image;}

         @SerializedName("date_sent")
         @Expose
         private String date_sent;



         @SerializedName("sent_message")
         @Expose
         private String sent_message;

         @SerializedName("date_recieved")
         @Expose
         private String date_recieved;

         @SerializedName("recieved_message")
         @Expose
         private String recieved_message;

         @SerializedName("date_shipped")
         @Expose
         private String date_shipped;

         @SerializedName("shipped_message")
         @Expose
         private String shipped_message;

         @SerializedName("date_complete")
         @Expose
         private String date_complete;

         @SerializedName("complete_message")
         @Expose
         private String complete_message;

         @SerializedName("image")
         @Expose
         private List<String> image;

     }

    }
    public class ViewOrderResponce{

        public Boolean getStatus() {return status;}

        public LatestOrder getLatest_order() {return Latest_order;}

        public List<OrderHistory> getOrder_history() {return Order_history;}

        @SerializedName("status")
        @Expose
        private Boolean status;

        @SerializedName("Latest_order")
        @Expose
        private LatestOrder Latest_order;

        @SerializedName("Order_history")
        @Expose
        private List<OrderHistory> Order_history;

       public class LatestOrder{

           public String getDate_sent() {return date_sent;}

           public String getSent_message() {return sent_message;}

           public List<String> getImage() {return image;}

           public String getDate_recieved() {return date_recieved;}

           public String getRecieved_message() {return recieved_message;}

           public String getDate_shipped() {return date_shipped;}

           public String getShipped_message() {return shipped_message;}

           public String getDate_complete() {return date_complete;}

           public String getComplete_message() {return complete_message;}

           @SerializedName("date_sent")
           @Expose
           private String date_sent;

           @SerializedName("sent_message")
           @Expose
           private String sent_message;

           @SerializedName("image")
           @Expose
           private List<String> image;

           @SerializedName("date_recieved")
           @Expose
           private String date_recieved;

           @SerializedName("recieved_message")
           @Expose
           private String recieved_message;

           @SerializedName("date_shipped")
           @Expose
           private String date_shipped;

           @SerializedName("shipped_message")
           @Expose
           private String shipped_message;

           @SerializedName("date_complete")
           @Expose
           private String date_complete;

           @SerializedName("complete_message")
           @Expose
           private String complete_message;


        }

         public class OrderHistory{
             public String getDate_sent() {return date_sent;}

             public String getSent_message() {return sent_message;}

             public List<String> getImage() {return image;}

             public String getRecieved_message() {return recieved_message;}

             public String getDate_recieved() {return date_recieved;}

             public String getDate_shipped() {return date_shipped;}

             public String getShipped_message() {return shipped_message;}

             public String getDate_complete() {return date_complete;}

             public String getComplete_message() {return complete_message;}

             @SerializedName("date_sent")
             @Expose
             private String date_sent;

             @SerializedName("sent_message")
             @Expose
             private String sent_message;

             @SerializedName("image")
             @Expose
             private List<String> image;

             @SerializedName("recieved_message")
             @Expose
             private String recieved_message;

             @SerializedName("date_recieved")
             @Expose
             private String date_recieved;

             @SerializedName("date_shipped")
             @Expose
             private String date_shipped;

             @SerializedName("shipped_message")
             @Expose
             private String shipped_message;

             @SerializedName("date_complete")
             @Expose
             private String date_complete;

             @SerializedName("complete_message")
             @Expose
             private String complete_message;

           }
    }
 public   class ShowProfileResponce{
        public Boolean getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getRegistered_from() {
            return registered_from;
        }

        public Data getData() {
            return data;
        }

        @SerializedName("status")
        @Expose
        private Boolean status;

        @SerializedName("message")
        @Expose
        private String message;

        @SerializedName("registered_from")
        @Expose
        private String registered_from;

        @SerializedName("data")
        @Expose
        private Data data;

        public class Data{
            public String getUser_id() {
                return user_id;
            }

            public String getName() {
                return name;
            }

            public String getMobile_no() {
                return mobile_no;
            }

            public String getProfile_image() {
                return profile_image;
            }

            public String getProfile_image_url() {return profile_image_url;}

            public String getEmail() {
                return email;
            }

            public Address getAddress() {
                return address;
            }

            public String getOrder_status() {
                return order_status;
            }

            @SerializedName("user_id")
            @Expose
            private String user_id;

            @SerializedName("name")
            @Expose
            private String name;

            @SerializedName("mobile_no")
            @Expose
            private String mobile_no;

            @SerializedName("profile_image")
            @Expose
            private String profile_image;

            @SerializedName("profile_image_url")
            @Expose
            private String profile_image_url;

            @SerializedName("email")
            @Expose
            private String email;

            public String getMobile_verify() {
                return mobile_verify;
            }

            @SerializedName("mobile_verify")
            @Expose
            private String mobile_verify;

            @SerializedName("address")
            @Expose
            private Address address;

            public   class Address{
                public String getAddress() {
                    return address;
                }

                public String getCity_name() {
                    return city_name;
                }

                public String getCity_id() {
                    return city_id;
                }

                public String getState_name() {
                    return state_name;
                }
                public String getState_id() {
                    return state_id;
                }
                public String getPin_code() {
                    return pin_code;
                }

                public String getComment() {
                    return comment;
                }

                @SerializedName("address")
                @Expose
                private String address;

                @SerializedName("city_name")
                @Expose
                private String city_name;

                @SerializedName("city_id")
                @Expose
                private String city_id;

                @SerializedName("state_name")
                @Expose
                private String state_name;

                @SerializedName("state_id")
                @Expose
                private String state_id;

                @SerializedName("pin_code")
                @Expose
                private String pin_code;

                @SerializedName("comment")
                @Expose
                private String comment;
            }
            @SerializedName("order_status")
            @Expose
            private String order_status;
        }
    }
    public   class SaveProfileResponce {
        public Boolean getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        @SerializedName("status")
        @Expose
        private Boolean status;

        @SerializedName("message")
        @Expose
        private String message;
    }
    public   class StateResponce {

        public int getStatus() {
            return status;
        }
        public String getMessage() {
            return message;
        }
        public List<Data> getData() {return data;}

        @SerializedName("status")
        @Expose
        private int status;

        @SerializedName("message")
        @Expose
        private String message;

        @SerializedName("data")
        @Expose
        private List<Data> data;

        public class Data{

            public String getState_id() {return state_id;}

            public String getState_name() {return state_name;}

            @SerializedName("state_id")
            @Expose
            private String state_id;

            @SerializedName("state_name")
            @Expose
            private String state_name;

        }
    }

    public   class CityResponce {

        public int getStatus() {
            return status;
        }
        public String getMessage() {
            return message;
        }
        public List<Data> getData() {return data;}

        @SerializedName("status")
        @Expose
        private int status;

        @SerializedName("message")
        @Expose
        private String message;

        @SerializedName("data")
        @Expose
        private List<Data> data;

        public class Data{

            public String getCity_id() {return city_id;}

            public String getCity_name() {return city_name;}

            @SerializedName("city_id")
            @Expose
            private String city_id;

            @SerializedName("city_name")
            @Expose
            private String city_name;

        }
    }
    public   class EditDeliveryAddResponce {

        public Boolean getStatus() {
            return status;
        }
        public String getMessage() {
            return message;
        }


        @SerializedName("status")
        @Expose
        private Boolean status;

        @SerializedName("message")
        @Expose
        private String message;


    }
    public class SocialLoginAddResponce {

        public int getStatus() {
            return status;
        }
        public String getMessage() {
            return message;
        }
        public String getUser_id() {return user_id;}
        public Data getData() {return data;}

        @SerializedName("status")
        @Expose
        private int status;

        @SerializedName("message")
        @Expose
        private String message;

        @SerializedName("user_id")
        @Expose
        private String user_id;

        @SerializedName("data")
        @Expose
        private Data data;

        public class Data{

            public String getUser_id() {return user_id;}

            @SerializedName("user_id")
            @Expose
            private String user_id;

            public String getMobile_no() {
                return mobile_no;
            }

            public String getMobile_verify() {
                return mobile_verify;
            }

            @SerializedName("mobile_no")
            @Expose
            private String mobile_no;

            @SerializedName("mobile_verify")
            @Expose
            private String mobile_verify;

        }
    }
    public class ResendOtpResponce {

        public Boolean getStatus() {
            return status;
        }
        public String getMessage() {
            return message;
        }
        public String getUser_id() {return mobile_no;}

        @SerializedName("status")
        @Expose
        private Boolean status;

        @SerializedName("message")
        @Expose
        private String message;

        @SerializedName("mobile_no")
        @Expose
        private String mobile_no;
    }

    public class NotificationResponce {

        public int getStatus() {
            return status;
        }
        public String getMessage() {
            return message;
        }
        public List<Data> getData() {return data;}

        @SerializedName("status")
        @Expose
        private int status;

        @SerializedName("message")
        @Expose
        private String message;

        @SerializedName("data")
        @Expose
        private List<Data> data;

        public class Data{

            public String getUser_id() {
                return user_id;
            }

            public String getMessage() {
                return message;
            }

            public String getNotification_date() {
                return notification_date;
            }

            public String getStatus() {
                return status;
            }

            @SerializedName("user_id")
            @Expose
            private String user_id;

            @SerializedName("message")
            @Expose
            private String message;

            @SerializedName("notification_date")
            @Expose
            private String notification_date;

            @SerializedName("status")
            @Expose
            private String status;
        }

    }
    public class QuestionResponce {

        public String getEligible_images() {
            return eligible_images;
        }

        public String getOrder_status() {
            return order_status;
        }

        public String getMessage() {
            return message;
        }

        public String getCount() {
            return count;
        }

        public Data getData() {
            return data;
        }

        public Boolean getStatus() {
            return status;
        }

        @SerializedName("status")
        @Expose
        private Boolean status;

        @SerializedName("eligible_images")
        @Expose
        private String eligible_images;

        @SerializedName("order_status")
        @Expose
        private String order_status;

        @SerializedName("message")
        @Expose
        private String message;

        @SerializedName("count")
        @Expose
        private String count;

        @SerializedName("data")
        @Expose
        private Data data;

        public class Data{

            public String getQuestion() {
                return question;
            }

            public List<String> getAnswer() {
                return answer;
            }

            @SerializedName("question")
            @Expose
            private String question;

            @SerializedName("answer")
            @Expose
            private List<String> answer;


        }

    }
    public class AnswerResponce {

        public Boolean getStatus() {
            return status;
        }
        public String getMessage() {
            return message;
        }


        @SerializedName("status")
        @Expose
        private Boolean status;

        @SerializedName("message")
        @Expose
        private String message;


    }
    public class ShareResponce {

        public Boolean getStatus() {
            return status;
        }
        public String getMessage() {
            return message;
        }


        @SerializedName("status")
        @Expose
        private Boolean status;

        @SerializedName("message")
        @Expose
        private String message;

        public String getOrder_status() {
            return order_status;
        }

        @SerializedName("order_status")
        @Expose
        private String order_status;

        public String getEligible_images() {
            return eligible_images;
        }

        @SerializedName("eligible_images")
        @Expose
        private String eligible_images;


    }

}

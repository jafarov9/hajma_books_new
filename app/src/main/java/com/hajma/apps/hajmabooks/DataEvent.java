package com.hajma.apps.hajmabooks;

public class DataEvent {

    public static class CallViewFragment {

        int response;

        public CallViewFragment(int response) {
            this.response = response;
        }

        public int getResponse() {
            return response;
        }

        public void setResponse(int response) {
            this.response = response;
        }
    }

    public static class CallAcceptRejectFollow {
       private int type;
       private int userID;

        public CallAcceptRejectFollow(int type, int userID) {
            this.type = type;
            this.userID = userID;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public static class CallProfileDetailsUpdate {

        int result;

        public CallProfileDetailsUpdate(int result) {
            this.result = result;
        }

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }
    }

    public static class CallCartUpdate {

        int result;

        public CallCartUpdate(int result) {
            this.result = result;
        }

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }
    }


    public static class CallPayInformation {

        private int bookId;
        private int paidType;
        private int toUserId;

        public CallPayInformation(int bookId, int paidType, int toUserId) {
            this.bookId = bookId;
            this.paidType = paidType;
            this.toUserId = toUserId;
        }

        public int getBookId() {
            return bookId;
        }

        public void setBookId(int bookId) {
            this.bookId = bookId;
        }

        public int getToUserId() {
            return toUserId;
        }

        public void setToUserId(int toUserId) {
            this.toUserId = toUserId;
        }

        public int getPaidType() {
            return paidType;
        }

        public void setPaidType(int paidType) {
            this.paidType = paidType;
        }
    }

}

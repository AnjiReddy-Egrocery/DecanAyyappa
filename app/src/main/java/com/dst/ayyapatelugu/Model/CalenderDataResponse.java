package com.dst.ayyapatelugu.Model;

import java.util.List;

public class CalenderDataResponse {

    private String status;
    private String errorCode;
    private List<Result> result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public class Result {

        private String year;
        private List<Poojas> poojasList;
        private String prevYear;
        private String nextYear;

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public List<Poojas> getPoojasList() {
            return poojasList;
        }

        public void setPoojasList(List<Poojas> poojasList) {
            this.poojasList = poojasList;
        }

        public String getPrevYear() {
            return prevYear;
        }

        public void setPrevYear(String prevYear) {
            this.prevYear = prevYear;
        }

        public String getNextYear() {
            return nextYear;
        }

        public void setNextYear(String nextYear) {
            this.nextYear = nextYear;
        }


        public class Poojas {

            private String monthName;
            private String openingDate;
            private String poojaName;
            private String closingDate;

            public String getMonthName() {
                return monthName;
            }

            public void setMonthName(String monthName) {
                this.monthName = monthName;
            }

            public String getOpeningDate() {
                return openingDate;
            }

            public void setOpeningDate(String openingDate) {
                this.openingDate = openingDate;
            }

            public String getPoojaName() {
                return poojaName;
            }

            public void setPoojaName(String poojaName) {
                this.poojaName = poojaName;
            }

            public String getClosingDate() {
                return closingDate;
            }

            public void setClosingDate(String closingDate) {
                this.closingDate = closingDate;
            }
        }
    }

}

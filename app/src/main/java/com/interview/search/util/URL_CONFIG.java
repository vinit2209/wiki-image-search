package com.interview.search.util;

public class URL_CONFIG {

        private final int thumbSize;
        private final int thumbnailLimit;
        public static class Builder {

            private  int thumbSize;
            private  int thumbnailLimit;

            public Builder thumbSize(int thumbSize) {
                this.thumbSize = thumbSize;
                return this;
            }

            public Builder thumbnailLimit(int thumbnailLimit) {
                this.thumbnailLimit = thumbnailLimit;
                return this;
            }

            public URL_CONFIG build() {
                return new URL_CONFIG(this);
            }
        }

        //private constructor to enforce object creation through builder
        private URL_CONFIG(Builder builder) {
            this.thumbSize = builder.thumbSize;
            this.thumbnailLimit = builder.thumbnailLimit;

        }



    public String getURL(){
        final String   URL = "https://en.wikipedia.org/w/api.php?" +
                "action=query&prop=pageimages&format=json&piprop=thumbnail&pithumbsize="+thumbSize+"&pilimit="+thumbnailLimit+"&generator=prefixsearch&gpssearch=";
        return URL;
    }

    public int getThumbSize(){
        return thumbSize;
    }

}
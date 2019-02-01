package com.naruto.popmovies.bean;

import java.util.List;

/**
 * 数据库中image_configurations.db的结构模型
 *
 * @author jelly.
 * @Date 2019-01-30.
 * @Time 14:02.
 */
public class ConfigurationBean {

    private ImagesBean images;
    private List<String> change_keys;

    public ImagesBean getImages() {
        return images;
    }

    public void setImages(ImagesBean images) {
        this.images = images;
    }

    public List<String> getChangeKeys() {
        return change_keys;
    }

    public void setChangeKeys(List<String> changeKeys) {
        this.change_keys = changeKeys;
    }

    public static class ImagesBean {

        private String base_url;
        private String secure_base_url;
        private List<String> backdrop_sizes;
        private List<String> logo_sizes;
        private List<String> poster_sizes;
        private List<String> profile_sizes;
        private List<String> still_sizes;

        public String getBaseUrl() {
            return base_url;
        }

        public void setBaseUrl(String baseUrl) {
            this.base_url = baseUrl;
        }

        public String getSecureBaseUrl() {
            return secure_base_url;
        }

        public void setSecureBaseUrl(String secureBaseUrl) {
            this.secure_base_url = secureBaseUrl;
        }

        public List<String> getBackdropSizes() {
            return backdrop_sizes;
        }

        public void setBackdropSizes(List<String> backdropSizes) {
            this.backdrop_sizes = backdropSizes;
        }

        public List<String> getLogoSizes() {
            return logo_sizes;
        }

        public void setLogoSizes(List<String> logoSizes) {
            this.logo_sizes = logoSizes;
        }

        public List<String> getPosterSizes() {
            return poster_sizes;
        }

        public void setPosterSizes(List<String> posterSizes) {
            this.poster_sizes = posterSizes;
        }

        public List<String> getProfileSizes() {
            return profile_sizes;
        }

        public void setProfileSizes(List<String> profileSizes) {
            this.profile_sizes = profileSizes;
        }

        public List<String> getStillSizes() {
            return still_sizes;
        }

        public void setStillSizes(List<String> stillSizes) {
            this.still_sizes = stillSizes;
        }
    }
}

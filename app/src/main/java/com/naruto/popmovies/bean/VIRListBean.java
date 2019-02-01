package com.naruto.popmovies.bean;

import com.naruto.popmovies.R;
import com.naruto.popmovies.data.Entry;
import com.naruto.popmovies.db.model.Genre;
import com.naruto.popmovies.stetho.MyApplication;

import java.util.List;

/**
 * 影片的视频和评论接口返回的数据类型
 *
 * @author jelly.
 * @Date 2019-01-27.
 * @Time 21:35.
 */
public class VIRListBean {

    private boolean adult;
    private String backdrop_path;
    private Object belongs_to_collection;
    private int budget;
    private String homepage;
    private int id;
    private String imdb_id;
    private String original_language;
    private String original_title;
    private String overview;
    private double popularity;
    private String poster_path;
    private String release_date;
    private int revenue;
    private int runtime;
    private String status;
    private String tagline;
    private String title;
    private boolean video;
    private double vote_average;
    private int vote_count;
    private VideosBean videos;
    private ImagesBean images;
    private ReviewsBean reviews;
    private List<Genre> genres;
    private List<ProductionCompaniesBean> production_companies;
    private List<ProductionCountriesBean> production_countries;
    private List<SpokenLanguagesBean> spoken_languages;

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getBackdropPath() {
        return backdrop_path != null ? Entry.BASE_IMAGE_URL + "/w300" + backdrop_path : "";
    }

    public void setBackdropPath(String backdropPath) {
        this.backdrop_path = backdropPath;
    }

    public Object getBelongsToCollection() {
        return belongs_to_collection;
    }

    public void setBelongsToCollection(Object belongsToCollection) {
        this.belongs_to_collection = belongsToCollection;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImdbId() {
        return imdb_id;
    }

    public void setImdbId(String imdbId) {
        this.imdb_id = imdbId;
    }

    public String getOriginalLanguage() {
        return original_language;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.original_language = originalLanguage;
    }

    public String getOriginalTitle() {
        return original_title;
    }

    public void setOriginalTitle(String originalTitle) {
        this.original_title = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return poster_path != null ? Entry.BASE_IMAGE_URL + "/w342" + poster_path : "";
    }

    public void setPosterPath(String posterPath) {
        this.poster_path = posterPath;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public void setReleaseDate(String releaseDate) {
        this.release_date = releaseDate;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTagLine() {
        return tagline;
    }

    public void setTagLine(String tagLine) {
        this.tagline = tagLine;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getVoteAverage() {
        return vote_average;
    }

    public void setVoteAverage(double voteAverage) {
        this.vote_average = voteAverage;
    }

    public int getVoteCount() {
        return vote_count;
    }

    public void setVoteCount(int voteCount) {
        this.vote_count = voteCount;
    }

    public VideosBean getVideos() {
        return videos;
    }

    public void setVideos(VideosBean videos) {
        this.videos = videos;
    }

    public ImagesBean getImages() {
        return images;
    }

    public void setImages(ImagesBean images) {
        this.images = images;
    }

    public ReviewsBean getReviews() {
        return reviews;
    }

    public void setReviews(ReviewsBean reviews) {
        this.reviews = reviews;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<ProductionCompaniesBean> getProductionCompanies() {
        return production_companies;
    }

    public void setProductionCompanies(List<ProductionCompaniesBean> productionCompanies) {
        this.production_companies = productionCompanies;
    }

    public List<ProductionCountriesBean> getProductionCountries() {
        return production_countries;
    }

    public void setProductionCountries(List<ProductionCountriesBean> productionCountries) {
        this.production_countries = productionCountries;
    }

    public List<SpokenLanguagesBean> getSpokenLanguages() {
        return spoken_languages;
    }

    public void setSpokenLanguages(List<SpokenLanguagesBean> spokenLanguages) {
        this.spoken_languages = spokenLanguages;
    }

    public static class VideosBean {
        private List<ResultsBean> results;

        public List<ResultsBean> getResults() {
            return results;
        }

        public void setResults(List<ResultsBean> results) {
            this.results = results;
        }

        public static class ResultsBean {

            private String id;
            private String iso_639_1;
            private String iso_3166_1;
            private String key;
            private String name;
            private String site;
            private int size;
            private String type;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getIso6391() {
                return iso_639_1;
            }

            public void setIso6391(String iso6391) {
                this.iso_639_1 = iso6391;
            }

            public String getIso31661() {
                return iso_3166_1;
            }

            public void setIso31661(String iso31661) {
                this.iso_3166_1 = iso31661;
            }

            public String getUrl() {
                if (key != null) {
                    if (key.contains(MyApplication.getContext().getString(R.string.value_name_youtube_id))) {
                        return Entry.BASE_YOUTUBE_URL + key;
                    } else {
                        return Entry.BASE_YOUTUBE_URL + MyApplication.getContext().getString(R.string.value_name_youtube_id) + key;
                    }
                } else {
                    return "";
                }
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getSite() {
                return site;
            }

            public void setSite(String site) {
                this.site = site;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }

    public static class ImagesBean {

        private List<BackdropsBean> backdrops;
        private List<PostersBean> posters;

        public List<BackdropsBean> getBackdrops() {
            return backdrops;
        }

        public void setBackdrops(List<BackdropsBean> backdrops) {
            this.backdrops = backdrops;
        }

        public List<PostersBean> getPosters() {
            return posters;
        }

        public void setPosters(List<PostersBean> posters) {
            this.posters = posters;
        }

        public static class BackdropsBean {
            /**
             * aspect_ratio : 1.777777777777778
             * file_path : /xu9zaAevzQ5nnrsXN6JcahLnG4i.jpg
             * height : 1080
             * iso_639_1 : null
             * vote_average : 5.552
             * vote_count : 19
             * width : 1920
             */

            private double aspect_ratio;
            private String file_path;
            private int height;
            private Object iso_639_1;
            private double vote_average;
            private int vote_count;
            private int width;

            public double getAspectRatio() {
                return aspect_ratio;
            }

            public void setAspectRatio(double aspectRatio) {
                this.aspect_ratio = aspectRatio;
            }

            public String getUrl() {
                return file_path != null ? Entry.BASE_IMAGE_URL + "/w300" + file_path : "";
            }

            public void setFilePath(String filePath) {
                this.file_path = filePath;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public Object getIso6391() {
                return iso_639_1;
            }

            public void setIso6391(Object iso6391) {
                this.iso_639_1 = iso6391;
            }

            public double getVoteAverage() {
                return vote_average;
            }

            public void setVoteAverage(double voteAverage) {
                this.vote_average = voteAverage;
            }

            public int getVoteCount() {
                return vote_count;
            }

            public void setVoteCount(int voteCount) {
                this.vote_count = voteCount;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }
        }

        public static class PostersBean {
            /**
             * aspect_ratio : 0.6668
             * file_path : /nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
             * height : 2500
             * iso_639_1 : en
             * vote_average : 6.062
             * vote_count : 66
             * width : 1667
             */

            private double aspect_ratio;
            private String file_path;
            private int height;
            private String iso_639_1;
            private double vote_average;
            private int vote_count;
            private int width;

            public double getAspectRatio() {
                return aspect_ratio;
            }

            public void setAspectRatio(double aspectRatio) {
                this.aspect_ratio = aspectRatio;
            }

            public String getUrl() {
                return file_path != null ? Entry.BASE_IMAGE_URL + "/w342" + file_path : "";
            }

            public void setFilePath(String filePath) {
                this.file_path = filePath;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public String getIso6391() {
                return iso_639_1;
            }

            public void setIso6391(String iso6391) {
                this.iso_639_1 = iso6391;
            }

            public double getVoteAverage() {
                return vote_average;
            }

            public void setVoteAverage(double voteAverage) {
                this.vote_average = voteAverage;
            }

            public int getVoteCount() {
                return vote_count;
            }

            public void setVoteCount(int voteCount) {
                this.vote_count = voteCount;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }
        }
    }

    public static class ReviewsBean {

        private int page;
        private int total_pages;
        private int total_results;
        private List<ResultsBeanX> results;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getTotalPages() {
            return total_pages;
        }

        public void setTotalPages(int totalPages) {
            this.total_pages = totalPages;
        }

        public int getTotalResults() {
            return total_results;
        }

        public void setTotalResults(int totalResults) {
            this.total_results = totalResults;
        }

        public List<ResultsBeanX> getResults() {
            return results;
        }

        public void setResults(List<ResultsBeanX> results) {
            this.results = results;
        }

        public static class ResultsBeanX {

            private String author;
            private String content;
            private String id;
            private String url;

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }

    public static class ProductionCompaniesBean {

        private int id;
        private String logo_path;
        private String name;
        private String origin_country;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLogoPath() {
            return logo_path != null ? Entry.BASE_IMAGE_URL + logo_path : "";
        }

        public void setLogoPath(String logoPath) {
            this.logo_path = logoPath;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOriginCountry() {
            return origin_country;
        }

        public void setOriginCountry(String originCountry) {
            this.origin_country = originCountry;
        }
    }

    public static class ProductionCountriesBean {
        private String iso_3166_1;
        private String name;

        public String getIso31661() {
            return iso_3166_1;
        }

        public void setIso31661(String iso31661) {
            this.iso_3166_1 = iso31661;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class SpokenLanguagesBean {

        private String iso_639_1;
        private String name;

        public String getIso6391() {
            return iso_639_1;
        }

        public void setIso6391(String iso6391) {
            this.iso_639_1 = iso6391;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

package trackme.test.com.myapplication.datamanager.model;

import java.util.List;

/**
 * Created by sarthak on 24/04/17.
 */

public class FlickrPhotoModel {

    private PhotosBean photos;
    private String stat;

    public PhotosBean getPhotos() {
        return photos;
    }

    public void setPhotos(PhotosBean photos) {
        this.photos = photos;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    @Override
    public String toString() {
        return "FlickrPhotoModel{" +
                "photos=" + photos +
                ", stat='" + stat + '\'' +
                '}';
    }

    public static class PhotosBean {
        private int page;
        private int pages;
        private int perpage;
        private int total;
        private List<PhotoBean> photo;

        @Override
        public String toString() {
            return "PhotosBean{" +
                    "page=" + page +
                    ", pages=" + pages +
                    ", perpage=" + perpage +
                    ", total=" + total +
                    ", photo=" + photo +
                    '}';
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public int getPerpage() {
            return perpage;
        }

        public void setPerpage(int perpage) {
            this.perpage = perpage;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<PhotoBean> getPhoto() {
            return photo;
        }

        public void setPhoto(List<PhotoBean> photo) {
            this.photo = photo;
        }

        public static class PhotoBean {
            private String id;
            private String owner;
            private String secret;
            private String server;
            private int farm;
            private String title;
            private int ispublic;
            private int isfriend;
            private int isfamily;
            private boolean isFlipped;

            @Override
            public String toString() {
                return "PhotoBean{" +
                        "id='" + id + '\'' +
                        ", owner='" + owner + '\'' +
                        ", secret='" + secret + '\'' +
                        ", server='" + server + '\'' +
                        ", farm=" + farm +
                        ", title='" + title + '\'' +
                        ", ispublic=" + ispublic +
                        ", isfriend=" + isfriend +
                        ", isfamily=" + isfamily +
                        ", isFlipped=" + isFlipped +
                        '}';
            }

            public boolean isFlipped() {
                return isFlipped;
            }

            public void setFlipped(boolean flipped) {
                isFlipped = flipped;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getOwner() {
                return owner;
            }

            public void setOwner(String owner) {
                this.owner = owner;
            }

            public String getSecret() {
                return secret;
            }

            public void setSecret(String secret) {
                this.secret = secret;
            }

            public String getServer() {
                return server;
            }

            public void setServer(String server) {
                this.server = server;
            }

            public int getFarm() {
                return farm;
            }

            public void setFarm(int farm) {
                this.farm = farm;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public int getIspublic() {
                return ispublic;
            }

            public void setIspublic(int ispublic) {
                this.ispublic = ispublic;
            }

            public int getIsfriend() {
                return isfriend;
            }

            public void setIsfriend(int isfriend) {
                this.isfriend = isfriend;
            }

            public int getIsfamily() {
                return isfamily;
            }

            public void setIsfamily(int isfamily) {
                this.isfamily = isfamily;
            }
        }
    }
}

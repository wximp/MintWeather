package com.wximp.mintweather.utils;

import java.util.List;

/**
 * Created by smile on 2016/12/13.
 */

public class SuggestionDao {
    private List<ResultsBean> results;

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * location : {"id":"WTW3SJ5ZBJUY","name":"上海","country":"CN","path":"上海,上海,中国","timezone":"Asia/Shanghai","timezone_offset":"+08:00"}
         * suggestion : {"car_washing":{"brief":"不宜","details":""},"dressing":{"brief":"较冷","details":""},"flu":{"brief":"较易发","details":""},"sport":{"brief":"较适宜","details":""},"travel":{"brief":"适宜","details":""},"uv":{"brief":"弱","details":""}}
         * last_update : 2016-12-13T19:10:14+08:00
         */

        private LocationBean location;
        private SuggestionBean suggestion;
        private String last_update;

        public LocationBean getLocation() {
            return location;
        }

        public void setLocation(LocationBean location) {
            this.location = location;
        }

        public SuggestionBean getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(SuggestionBean suggestion) {
            this.suggestion = suggestion;
        }

        public String getLast_update() {
            return last_update;
        }

        public void setLast_update(String last_update) {
            this.last_update = last_update;
        }

        public static class LocationBean {
            /**
             * id : WTW3SJ5ZBJUY
             * name : 上海
             * country : CN
             * path : 上海,上海,中国
             * timezone : Asia/Shanghai
             * timezone_offset : +08:00
             */

            private String id;
            private String name;
            private String country;
            private String path;
            private String timezone;
            private String timezone_offset;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getTimezone() {
                return timezone;
            }

            public void setTimezone(String timezone) {
                this.timezone = timezone;
            }

            public String getTimezone_offset() {
                return timezone_offset;
            }

            public void setTimezone_offset(String timezone_offset) {
                this.timezone_offset = timezone_offset;
            }
        }

        public static class SuggestionBean {
            /**
             * car_washing : {"brief":"不宜","details":""}
             * dressing : {"brief":"较冷","details":""}
             * flu : {"brief":"较易发","details":""}
             * sport : {"brief":"较适宜","details":""}
             * travel : {"brief":"适宜","details":""}
             * uv : {"brief":"弱","details":""}
             */

            private CarWashingBean car_washing;
            private DressingBean dressing;
            private FluBean flu;
            private SportBean sport;
            private TravelBean travel;
            private UvBean uv;

            public CarWashingBean getCar_washing() {
                return car_washing;
            }

            public void setCar_washing(CarWashingBean car_washing) {
                this.car_washing = car_washing;
            }

            public DressingBean getDressing() {
                return dressing;
            }

            public void setDressing(DressingBean dressing) {
                this.dressing = dressing;
            }

            public FluBean getFlu() {
                return flu;
            }

            public void setFlu(FluBean flu) {
                this.flu = flu;
            }

            public SportBean getSport() {
                return sport;
            }

            public void setSport(SportBean sport) {
                this.sport = sport;
            }

            public TravelBean getTravel() {
                return travel;
            }

            public void setTravel(TravelBean travel) {
                this.travel = travel;
            }

            public UvBean getUv() {
                return uv;
            }

            public void setUv(UvBean uv) {
                this.uv = uv;
            }

            public static class CarWashingBean {
                /**
                 * brief : 不宜
                 * details :
                 */

                private String brief;
                private String details;

                public String getBrief() {
                    return brief;
                }

                public void setBrief(String brief) {
                    this.brief = brief;
                }

                public String getDetails() {
                    return details;
                }

                public void setDetails(String details) {
                    this.details = details;
                }
            }

            public static class DressingBean {
                /**
                 * brief : 较冷
                 * details :
                 */

                private String brief;
                private String details;

                public String getBrief() {
                    return brief;
                }

                public void setBrief(String brief) {
                    this.brief = brief;
                }

                public String getDetails() {
                    return details;
                }

                public void setDetails(String details) {
                    this.details = details;
                }
            }

            public static class FluBean {
                /**
                 * brief : 较易发
                 * details :
                 */

                private String brief;
                private String details;

                public String getBrief() {
                    return brief;
                }

                public void setBrief(String brief) {
                    this.brief = brief;
                }

                public String getDetails() {
                    return details;
                }

                public void setDetails(String details) {
                    this.details = details;
                }
            }

            public static class SportBean {
                /**
                 * brief : 较适宜
                 * details :
                 */

                private String brief;
                private String details;

                public String getBrief() {
                    return brief;
                }

                public void setBrief(String brief) {
                    this.brief = brief;
                }

                public String getDetails() {
                    return details;
                }

                public void setDetails(String details) {
                    this.details = details;
                }
            }

            public static class TravelBean {
                /**
                 * brief : 适宜
                 * details :
                 */

                private String brief;
                private String details;

                public String getBrief() {
                    return brief;
                }

                public void setBrief(String brief) {
                    this.brief = brief;
                }

                public String getDetails() {
                    return details;
                }

                public void setDetails(String details) {
                    this.details = details;
                }
            }

            public static class UvBean {
                /**
                 * brief : 弱
                 * details :
                 */

                private String brief;
                private String details;

                public String getBrief() {
                    return brief;
                }

                public void setBrief(String brief) {
                    this.brief = brief;
                }

                public String getDetails() {
                    return details;
                }

                public void setDetails(String details) {
                    this.details = details;
                }
            }
        }
    }
}

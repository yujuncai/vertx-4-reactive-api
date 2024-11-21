package org.limadelrey.vertx4.reactive.rest.api.message;

public class KeyVo implements  Comparable<KeyVo>{

    private  String key;

    private  Integer lengthOrsize;

    private  Integer ttl;

    private  String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public KeyVo(String key, Integer lengthOrsize, Integer ttl) {
        this.key = key;
        this.lengthOrsize = lengthOrsize;
        this.ttl = ttl;
    }
    public KeyVo(){}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public Integer getLengthOrsize() {
        return lengthOrsize;
    }

    public void setLengthOrsize(Integer lengthOrsize) {
        this.lengthOrsize = lengthOrsize;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    @Override
    public int compareTo(KeyVo o) {
        //System.out.println(Integer.compare(1, 2));  -1
       // System.out.println(Integer.compare(3, 2));   1
      //  System.out.println(Integer.compare(2, 2));   0

        if(this.lengthOrsize.intValue()==o.lengthOrsize.intValue()){
            return 0;
        }else if (this.lengthOrsize.intValue()>o.lengthOrsize.intValue()){
            return -1;
        }else if (this.lengthOrsize.intValue()<o.lengthOrsize.intValue()) {
            return 1;
        }else {
            return 0;
        }


    }
}

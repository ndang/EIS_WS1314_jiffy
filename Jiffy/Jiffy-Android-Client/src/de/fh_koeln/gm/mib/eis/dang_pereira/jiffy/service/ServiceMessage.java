package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.service;

public class ServiceMessage {

        private String topic;
        private String msg;
        
        public ServiceMessage(String topic, String msg) {
                this.topic = topic;
                this.msg = msg;
        }
        
        public String getTopic() {
                return this.topic;
        }
        
        
        public String getMsg() {
                return this.msg;
        }
}
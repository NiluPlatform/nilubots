package tech.nilu.bots.proxy.web3;

public enum Bip39Locale {
        CHINESE_SIMPLIFIED("zh_CN"), CHINESE_TRADITIONAL("zh_TW"), JAPANESE("jp")
        , FRENCH("fr"), ENGLISH("en"), RUSSIAN("ru"), KOREAN("ko"), ESPANISH("es"), ITALIAN("it");

        private String locale;

        Bip39Locale(String locale) {
            this.locale = locale;
        }

        public String getLocale() {
            return locale;
        }
    }

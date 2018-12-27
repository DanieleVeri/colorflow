package com.colorflow.database;

/**
 * Created by daniele on 29/07/17.
 */

final class DbStructure {

    public static final String NAME = "Data.db";
    public static final int VERSION = 1;
    public enum PurchasedType {
        RING, BONUS
    }
    public enum StatusKey {
        COINS, RECORD, VERSION
    }

    private DbStructure() {
    }

    public class Tables {
        private Tables() {
        }

        public class STATUS {
            private STATUS() {
            }

            public static final String
                    name = "STATUS",
                    KEY = "KEY",
                    VALUE = "VALUE";
        }

        public class PURCHASED {
            private PURCHASED() {
            }

            public static final String
                    name = "PURCHASED",
                    TYPE = "TYPE",
                    ID = "ID",
                    USED = "USED";
        }

        public class STAGE {
            private STAGE() {
            }

            public static final String
                    name = "STAGE",
                    NAME = "NAME",
                    RECORD = "RECORD";
        }
    }

    public class Query {

        public Query() {
        }

        public static final String
                CREATE_STATUS = "CREATE TABLE STATUS(" +
                        "KEY TEXT," +
                        "VALUE TEXT," +
                        "PRIMARY KEY(KEY));",
                CREATE_PURCHASED = "CREATE TABLE PURCHASED(" +
                        "TYPE TEXT," +
                        "ID TEXT," +
                        "USED INTEGER," +
                        "PRIMARY KEY(TYPE, ID));",
                CLEAR_PURCHASED = "DELETE FROM PURCHASED",
                DELETE_TABLES = "DROP TABLE IF EXISTS PURCHASED, STATUS, STAGE";
    }

}

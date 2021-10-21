DROP TABLE IF EXISTS "public"."file_upload_info";
CREATE TABLE "public"."file_upload_info" (
  "id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "master_id" varchar(50) COLLATE "pg_catalog"."default",
  "prefix" varchar(50) COLLATE "pg_catalog"."default",
  "suffix" varchar(50) COLLATE "pg_catalog"."default",
  "file_type" varchar(50) COLLATE "pg_catalog"."default",
  "file_name" varchar(100) COLLATE "pg_catalog"."default",
  "file_size" float8,
  "upload_time" timestamp(0),
  "mongo_id" varchar(100) COLLATE "pg_catalog"."default",
  "mongo_thum_id" varchar(100) COLLATE "pg_catalog"."default",
  "is_deleted" varchar(1) COLLATE "pg_catalog"."default",
  "deleted_time" timestamp(0)
)
;
COMMENT ON COLUMN "public"."file_upload_info"."id" IS '主键';
COMMENT ON COLUMN "public"."file_upload_info"."master_id" IS '业务 ID';
COMMENT ON COLUMN "public"."file_upload_info"."prefix" IS '业务前缀';
COMMENT ON COLUMN "public"."file_upload_info"."suffix" IS '业务后缀';
COMMENT ON COLUMN "public"."file_upload_info"."file_type" IS '文件类型';
COMMENT ON COLUMN "public"."file_upload_info"."file_name" IS '文件名';
COMMENT ON COLUMN "public"."file_upload_info"."file_size" IS '文件大小';
COMMENT ON COLUMN "public"."file_upload_info"."upload_time" IS '上传时间';
COMMENT ON COLUMN "public"."file_upload_info"."mongo_id" IS 'MongoDB 文件 ID';
COMMENT ON COLUMN "public"."file_upload_info"."mongo_thum_id" IS 'MongoDB 缩略图 ID(图片压缩)';
COMMENT ON COLUMN "public"."file_upload_info"."is_deleted" IS '逻辑删除标记';
COMMENT ON COLUMN "public"."file_upload_info"."deleted_time" IS '删除时间';

-- ----------------------------
-- Primary Key structure for table file_upload_info
-- ----------------------------
ALTER TABLE "public"."file_upload_info" ADD CONSTRAINT "file_upload_to_mongo_info_pkey" PRIMARY KEY ("id");

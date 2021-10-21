var vm = new Vue({
    el: '#app',
    data() {
        return {
            fileList: [],
            /*showDoc: false,*/
            showPdf: false,
            showImg: false,
            imagesUrl: "",
            pdfUrl: "",
            // 上传文件个数限制
            limit: 10,
            fileData: "",
            // 文件预览 dialog 宽
            fileWidth: "min-width = 300px",
            fileInfo: {
                masterId: '',
                prefix: '',
                suffix: '',
                fileType: '',
                fileName: '',
            },
        };
    },

    mounted() {
        this.getFileList();
    },

    methods: {
        getFileList() {
            let that = this;
            that.fileList = [];
            let params = {
                master_id: that.fileInfo.masterId,
                prefix: that.fileInfo.prefix,
                suffix: that.fileInfo.suffix,
                fileType: that.fileInfo.fileType,
                fileName: that.fileInfo.fileName,
            };

            $.ajax({
                url: ctx + 'fileUtilsMongo/getFilesByCustomize',
                type: "get",
                async: false,
                data: params,
                success(data) {
                    data.forEach(item => {
                        let res = ctx + 'fileUtilsMongo/downloadFile?file_id=' + item.mongoId;
                        that.fileList.push({
                            name: item.fileName,
                            type: '.' + item.fileType,
                            url: res,
                            mongoId: item.mongoId
                        });
                    })
                }, error() {
                    that.$message({
                        message: '服务器走丢了',
                        type: 'error'
                    })
                }
            })
        },

        // 文件超出个数限制时的钩子
        handleExceed(files, fileList) {
            this.$message.error(`每次只能上传 ${this.limit} 个文件`);
        },

        // 删除文件之前的钩子
        /*beforeRemove(file, fileList) {
            return this.$confirm(`确定移除 ${file.name}?`);
        },*/

        // 文件上传成功时的钩子
        /*handleSuccess(response, file, fileList) {
            this.fileList = fileList;
        },*/

        // 文件上传前的校验
        beforeAvatarUpload(file) {
            let fileSizeLimit = file.size / 1024 / 1024 < 100;
            let fileNameLimit = file.name.length < 30;

            if (!fileSizeLimit) {
                this.$message.error("上传文件大小要小于 100M 哦");
                return false;
            }
            if (!fileNameLimit) {
                this.$message.error("上传文件名称长度必须要小于30个文字哦");
                return false;
            }
        },

        uploadFile(fileObj) {
            let that = this;

            let masterId = that.genUUId(),
                prefix = 'master_prefix',
                suffix = 'master_suffix';
            let config = {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            };

            that.fileData = new FormData();
            that.fileData.append('files', fileObj.file);

            // 调用 $refs.uploadFiles.submit() 会触发:http-request = "uploadFile"事件
            // that.$refs.uploadFiles.submit();

            var url = ctx + "fileUtilsMongo/uploadFile?masterId=" + masterId + "&prefix=" + prefix + "&suffix=" + suffix;
            axios.post(url, that.fileData, config).then(({data: {code}}) => {
                if (code == 200) {
                    that.$message.success('上传成功');
                    that.getFileList();
                }
            }).catch(res => {
                that.$message.error("服务器走丢了");
            });
        },

        // 文件预览
        previewFile(fileUrl, fileType) {
            /*if (fileType.indexOf("doc") != -1 || fileType.indexOf("docx") != -1 ||
                fileType.indexOf("xls") != -1 || fileType.indexOf("xlsx") != -1 ||
                fileType.indexOf("ppt") != -1) {
                this.fileWidth = '80%';
                this.showDoc = true;
            } else */
            if (fileType.indexOf("pdf") != -1) {
                this.fileWidth = '80%';
                this.pdfUrl = 'vue-pdf/web/viewer.html?file=' + encodeURIComponent(fileUrl);
                this.showPdf = true;
            } else if (fileType.indexOf("jpg") != -1 || fileType.indexOf("png") != -1 ||
                fileType.indexOf("jpeg") != -1) {
                this.showImg = true;
                this.imagesUrl = fileUrl;
                let image = new Image();
                image.src = fileUrl;
                image.onload = () => {
                    this.fileWidth = image.width + 'px';
                };
            } else {
                this.$message.warning("当前文件暂不支持预览")
            }
        },

        closePreviewClick() {
            /* if (this.showDoc == true) {
                 this.showDoc = false
             } else */
            if (this.showPdf == true) {
                this.showPdf = false
            } else if (this.showImg == true) {
                this.showImg = false
            }
        },

        // 删除文件
        delFile(mongoId) {
            let that = this;

            that.fileList.map((item, index) => {
                if (item.mongoId == mongoId) {
                    $.ajax({
                        url: ctx + 'fileUtilsMongo/deleteFileByMongoId',
                        type: "post",
                        async: false,
                        data: {mongoIds: mongoId},
                        success(data) {
                            that.$message({
                                message: '删除成功',
                                type: 'success'
                            });

                            that.fileList.splice(index, 1);
                        }, error() {
                            that.$message({
                                message: '服务器走丢了',
                                type: 'error'
                            })
                        }
                    })
                }
            })
        },

        // 下载文件
        downloadFile(fileUrl) {
            window.location.href = fileUrl;
        },

        // 生成UUID
        genUUId() {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                let r = Math.random() * 16 | 0,
                    v = c == 'x' ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
        },
    },
});
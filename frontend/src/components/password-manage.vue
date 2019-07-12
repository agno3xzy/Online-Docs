<template>
<div>
    <backgroundimg></backgroundimg>
    <div id="change-form">
        <el-button type="text" icon="el-icon-arrow-left" id="back" @click="toDocumentManage">返回</el-button>
        <el-form :model="form" label-position="right" ref="passwordForm" size="mini" :rules="rule" label-width="80px">
            <el-form-item label="旧密码" prop="oldPassword">
                <el-input type="password" v-model="form.oldPassword" size="mini"></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
                <el-input type="password" v-model="form.newPassword" size="mini"></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
                <el-input type="password" v-model="form.confirmPassword" size="mini"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" round @click="changePassword" style="width:100px;margin-top:10px;">确认修改</el-button>
            </el-form-item>
        </el-form>`
    </div>
</div>
</template>

<script>
import backgroundimg from './background-img'
export default {
    data() {
        var validateOldPassword = (rule,value,callback) => {
            if(!value)
            {
                callback (new Error('密码不能为空'))
            } else
            {
                callback()
            }
        };
        var validateNewPassword = (rule,value,callback) => {
            if(!value)
            {
                callback (new Error('密码不能为空'))
            } 
            console.log(value)
            console.log(this.form.oldPassword)
            if(value === this.form.oldPassword)
            {
                callback (new Error('不能和旧密码一致'))
            }  else
            {
                callback()
            }
        };
        var validateConfirmPassword = (rule,value,callback) => {
            if(!value)
            {
                return callback(new Error('确认密码不能为空'))
            }
            if(value !== this.form.newPassword)
            {
                callback(new Error('两次密码不一致'))
            } else
            {
                callback()
            }
        };
        return {
            form: {
                oldPassword: '',
                newPassword: '',
                confirmPassword: ''
            },
            rule: {
                oldPassword: [{validator: validateOldPassword, trigger: 'blur'}],
                newPassword: [
                    {validator: validateNewPassword, trigger: 'blur'},
                    {min: 6, message: '密码至少6位', trigger: 'blur'}
                ],
                confirmPassword: [{validator: validateConfirmPassword, trigger: 'blur'}],
            }
        }
    },
    components: {backgroundimg},
    methods: {
        changePassword() {
            this.$refs['passwordForm'].validate((valid) => {
                if(valid)
                {
                    this.$axios(
                    {
                        url:'/modifyKey',
                        method:"post",
                        data:{
                            username: window.sessionStorage.username,
                            oldPassword: this.form.oldPassword,
                            newPassword: this.form.newPassword
                        },
                        transformRequest: [function (data) {
                        // Do whatever you want to transform the data
                        let ret = ''
                        for (let it in data) {
                        // 如果要发送中文 编码 
                            ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
                        }
                        return ret
                        }],
                        headers: {
                            'Content-Type':'application/x-www-form-urlencoded'
                        }
                    }).catch(error => {
                        console.log(error.message);
                    })
                    .then(response => {
                        if(response.data.message === 'success')
                        {
                            this.$message({
                                message: '修改成功',
                                type: 'success'
                            })
                            this.$router.push({
                                path: '/document-manage'
                            })
                        } else if(response.data.message === 'fail')
                        {
                            this.$message({
                                message: '密码错误，请重试',
                                type: 'error'
                            })
                        }
                    });
                }
            })
            
        },
        toDocumentManage() {
            this.$router.push({
                path: '/document-manage'
            })
        }
    }
}
</script>

<style scoped>
@import '../../static/css/keyframe.css';
#change-form {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%,-50%);
    width: 400px;
    background-color: rgba(245,245,245,0.6);
    border-radius: 5px;
    opacity: 0;
    animation: fadeIn 1s ease 0.5s 1;
    animation-fill-mode: forwards;
}
#back {
    position: absolute;
    left: 5%;
    top: 5%;
}
.el-form {
    width: 250px;
    position: relative;
    margin-top: 70px;
    margin-left: 17%;
}
</style>
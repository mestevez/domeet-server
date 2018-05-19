<template>
  <v-card class="dm-drop-full">
    <v-card-title primary-title>
      <div class="headline">{{ i18n.title_files }} ({{ meetData.documents.length }})</div>
    </v-card-title>
    <v-card-text>
      <v-container flex xs12 class="dm-drop-full">
        <div v-show="$refs.upload && $refs.upload.dropActive" class="drop-active">
          <h3>{{ i18n.message_drop_here }}</h3>
        </div>
        <v-data-table
          :headers="headers"
          :items="meet_files"
          :loading="loading > 0"
          hide-actions
          class="elevation-1"
        >
          <template slot="items" slot-scope="props">
            <td class="cell-name text-xs ellipsis">
              <a v-if="props.item.url != null" :href="props.item.url">{{ props.item.name }}</a>
              <template v-else>{{ props.item.name }}</template>
            </td>
            <td class="text-xs-right">{{ humanReadableSize(props.item.size) }}</td>
            <td>
              <v-chip v-if="props.item.status === MeetDocStatus.PENDING" color="warning" text-color="white">{{ i18n.label_file_status_pending }}</v-chip>
              <v-chip v-else-if="props.item.status === MeetDocStatus.UPLOADING" color="warning" text-color="white">{{ i18n.label_file_status_uploading }}</v-chip>
              <v-chip v-else-if="props.item.status === MeetDocStatus.ERROR" color="error" text-color="white">{{ i18n.label_file_status_error }}</v-chip>
              <v-chip v-else-if="props.item.status === MeetDocStatus.SUCCEED" color="success" text-color="white">{{ i18n.label_file_status_succeed }}</v-chip>
              <v-chip v-else-if="props.item.status === MeetDocStatus.CLOUD" color="primary" text-color="white">{{ i18n.label_file_status_cloud }}</v-chip>
              <v-chip v-else-if="props.item.status === MeetDocStatus.DELETED" color="secondary" text-color="white">{{ i18n.label_file_status_deleted }}</v-chip>
            </td>
            <td class="justify-end layout px-0">
              <v-btn v-if="props.item.status === MeetDocStatus.UPLOADING" icon class="mx-0" @click="abortUpload(props.item)">
                <v-icon color="error">cancel</v-icon>
              </v-btn>
              <v-btn v-else-if="props.item.status <= MeetDocStatus.ERROR" icon class="mx-0" @click="uploadFile(props.item)">
                <v-icon color="success">cloud_upload</v-icon>
              </v-btn>
              <v-btn icon class="mx-0" @click="deleteFile(props.item)" :disabled="props.item.status === MeetDocStatus.DELETED">
                <v-icon color="pink">delete</v-icon>
              </v-btn>
            </td>
          </template>
          <template slot="no-data">
            <div class="text-center pa-5">
              <h4>{{ i18n.message_drop_here }}</h4>
            </div>
          </template>
        </v-data-table>
      </v-container>
    </v-card-text>
    <v-card-actions>
      <file-upload
        :multiple="true"
        :drop="true"
        :drop-directory="true"
        :post-action="'/app/meet/' + meetData.meet_id + '/meet_doc'"
        @input-file="inputFile"
        @input-filter="inputFilter"
        v-model="meet_files"
        ref="upload"
      ></file-upload>
      <v-btn
        color="success"
        ref="uploadbtn"
        @click.prevent="$refs.upload.$el.click()">{{ i18n.label_upload_files }}</v-btn>
      <v-btn
        v-if="!$refs.upload || !$refs.upload.active"
        @click.prevent="startUpload"
        :disabled="pending_files.length === 0"
        color="primary"
      >{{ i18n.label_upload_start }}</v-btn>
      <v-btn
        v-if="$refs.upload && $refs.upload.active"
        @click.prevent="stopUpload"
        color="error"
      >{{ i18n.label_upload_stop }}</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
import FileUpload from 'vue-upload-component'
import { MeetDocStatus } from '@/model/meet_doc'
import filesize from 'filesize'

export default {
  name: 'MeetFilesEdit',
  components: {
    FileUpload
  },
  props: ['source', 'meetData', 'i18nData', 'isLeader'],
  computed: {
    pending_files: function () {
      return this.meet_files.filter((file) => { return file.status < MeetDocStatus.SUCCEED })
    },
    meet_files: {
      get: function () {
        return this.meetData.documents.map((item) => {
          let convItem = item
          convItem.name = item.doc_name
          convItem.size = item.doc_size
          convItem.url = this.meetData.getURL('/app/meet/{meet_id}/meet_doc/{doc_id}', { meet_id: this.meetData.meet_id, doc_id: item.doc_id })
          if (typeof convItem.status === 'undefined') {
            convItem.status = MeetDocStatus.CLOUD
          }
          return convItem
        })
      },
      set: function (meetFiles) {
        this.meetData.documents = meetFiles.map((item) => {
          let convItem = item
          convItem.doc_name = item.name
          convItem.doc_size = item.size

          if (typeof convItem.status === 'undefined' || convItem.status < MeetDocStatus.CLOUD) {
            if (item.success) {
              convItem.status = MeetDocStatus.SUCCEED
            } else if (item.error) {
              convItem.status = MeetDocStatus.ERROR
            } else if (item.progress === '0.00') {
              convItem.status = MeetDocStatus.PENDING
            } else {
              convItem.status = MeetDocStatus.UPLOADING
            }
          }
          return convItem
        })
      }
    }
  },
  data () {
    return {
      headers: [
        { text: 'Name', value: 'doc_name', width: '20px' },
        { text: 'Size', value: 'doc_size', align: 'right' },
        { text: 'Status', value: 'doc_name', align: 'center' },
        { text: '', value: 'doc_name', align: 'center', sortable: false }
      ],
      loading: 0,
      MeetDocStatus: MeetDocStatus,
      i18n: Object.assign({
        message_drop_here: 'Drop files to upload',
        label_file_status_pending: 'Pending',
        label_file_status_uploading: 'Uploading',
        label_file_status_error: 'Error',
        label_file_status_succeed: 'Succeed',
        label_file_status_cloud: 'Cloud',
        label_file_status_deleted: 'Deleted',
        label_upload_files: 'Upload file',
        label_upload_start: 'Start upload',
        label_upload_stop: 'Stop upload',
        title_files: 'Files'
      }, this.i18nData)
    }
  },
  methods: {

    humanReadableSize: function (size) {
      return filesize(size)
    },

    /**
     * Has changed
     * @param  Object|undefined   newFile   Read only
     * @param  Object|undefined   oldFile   Read only
     * @return undefined
     */
    inputFile: function (newFile, oldFile) {
      if (newFile && oldFile && !newFile.active && oldFile.active) {
        // Get response data
        if (newFile.xhr) {
          //  Get the response status code
          // console.log('status', newFile.xhr.status)
          this.source.$unlockFetch(this.meetData)
        }
      }
    },
    /**
     * Pretreatment
     * @param  Object|undefined   newFile   Read and write
     * @param  Object|undefined   oldFile   Read only
     * @param  Function           prevent   Prevent changing
     * @return undefined
     */
    inputFilter: function (newFile, oldFile, prevent) {
      // if (newFile && !oldFile) {
      //   // Filter non-image file
      //   if (!/\.(jpeg|jpe|jpg|gif|png|webp)$/i.test(newFile.name)) {
      //     return prevent()
      //   }
      // }

      // Create a blob field
      if (newFile && newFile.status !== MeetDocStatus.DELETED) {
        newFile.blob = ''
        let URL = window.URL || window.webkitURL
        if (URL && URL.createObjectURL) {
          newFile.blob = URL.createObjectURL(newFile.file)
        }
      }
    },

    deleteFile: function (file) {
      if (file.status < MeetDocStatus.CLOUD) {
        this.$refs.upload.remove(file)
      } else {
        file.status = MeetDocStatus.DELETED
        this.loading++
        this.source.$lockFetch(this.meetData)
        this.meetData.getRequest({
          url: this.meetData.getURL('/app/meet/{meet_id}/meet_doc/{doc_id}', { meet_id: this.meetData.meet_id, doc_id: file.doc_id }),
          method: 'DELETE'
        }).send().then(() => {
          this.loading--
          this.source.$unlockFetch(this.meetData)
        }).catch(() => {
          this.loading--
          this.source.$unlockFetch(this.meetData)
        })
      }
    },

    uploadFile: function (file) {
      this.source.$lockFetch(this.meetData)
      this.$refs.upload.update(file, {active: true, error: '', progress: '0.00'})
    },

    startUpload: function () {
      this.pending_files.forEach((file) => {
        this.$refs.upload.update(file, {error: '', progress: '0.00'})
        this.source.$lockFetch(this.meetData)
      })

      this.$refs.upload.active = true
    },

    abortUpload: function (file) {
      this.$refs.upload.update(file, {active: false})
    },

    stopUpload: function () {
      this.$refs.upload.active = false
    }
  }
}
</script>

<style scoped>
.dm-drop-full .cell-name {
  width: 30%;
  max-width: 200px;
}
.dm-drop-full .drop-active {
  top: 0;
  bottom: 0;
  right: 0;
  left: 0;
  position: fixed;
  z-index: 9999;
  opacity: .6;
  text-align: center;
  background: #000;
}
.dm-drop-full .drop-active h3 {
  margin: -.5em 0 0;
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  -webkit-transform: translateY(-50%);
  -ms-transform: translateY(-50%);
  transform: translateY(-50%);
  font-size: 40px;
  color: #fff;
  padding: 0;
}
</style>

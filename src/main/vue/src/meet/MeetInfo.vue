<template>
  <v-app id="meetinfo">
    <v-navigation-drawer
      fixed
      v-model="notificationsPanel"
      right
      clipped
      app
      permanent
      width="150"
    >
    </v-navigation-drawer>
    <apptoolbar></apptoolbar>
    <v-content>
      <v-layout row wrap>
        <v-flex xs8><h3 class="display-2">{{ app.meet.meet_title }}</h3></v-flex>
        <v-flex xs4 class="text-xs-right" style="align-self: flex-end">
          <v-icon color="warning">{{ getIconStar(1, this.rate) }}</v-icon>
          <v-icon color="warning">{{ getIconStar(2, this.rate) }}</v-icon>
          <v-icon color="warning">{{ getIconStar(3, this.rate) }}</v-icon>
          <v-icon color="warning">{{ getIconStar(4, this.rate) }}</v-icon>
          <v-icon color="warning">{{ getIconStar(5, this.rate) }}</v-icon>
        </v-flex>
        <v-flex xs12 class="text-xs-right">
          <v-dialog v-if="myrate != null && myrate.attd_rate == null" v-model="showRateDialog" persistent max-width="500px">
            <a slot="activator" >{{ i18n.btn_rate }}</a>
            <v-card>
              <v-card-title>
                <span class="headline">{{  i18n.title_rate}}</span>
              </v-card-title>
              <v-card-text>
                <v-layout column>
                  <v-flex>
                    <v-btn flat icon color="warning" class="ma-0" @click.native="myrate.attd_rate = myrate.attd_rate == 1 ? 0 : 1">
                      <v-icon>{{ getIconStar(1, myrate.attd_rate) }}</v-icon>
                    </v-btn>
                    <v-btn flat icon color="warning" class="ma-0" @click.native="myrate.attd_rate = 2">
                      <v-icon>{{ getIconStar(2, myrate.attd_rate) }}</v-icon>
                    </v-btn>
                    <v-btn flat icon color="warning" class="ma-0" @click.native="myrate.attd_rate = 3">
                      <v-icon>{{ getIconStar(3, myrate.attd_rate) }}</v-icon>
                    </v-btn>
                    <v-btn flat icon color="warning" class="ma-0" @click.native="myrate.attd_rate = 4">
                      <v-icon>{{ getIconStar(4, myrate.attd_rate) }}</v-icon>
                    </v-btn>
                    <v-btn flat icon color="warning" class="ma-0" @click.native="myrate.attd_rate = 5">
                      <v-icon>{{ getIconStar(5, myrate.attd_rate) }}</v-icon>
                    </v-btn>
                  </v-flex>
                  <v-flex>
                    <v-text-field
                      :label="i18n.label_attd_comment"
                      v-model="myrate.attd_comment"
                      required
                      multi-line
                    ></v-text-field>
                  </v-flex>
                </v-layout>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" flat @click="cancelRate">{{ i18n.btn_close }}</v-btn>
                <v-btn color="success" flat @click="saveRate">{{ i18n.btn_rate }}</v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </v-flex>
      </v-layout>
      <p>{{ app.meet.meet_description }}</p>
      <div>
        <div>{{ i18n.label_meet_time_start }}: {{ parseDate(app.meet.meet_time_start) }}</div>
        <div>{{ i18n.label_meet_duration }}: {{ app.meet.meet_duration }}</div>
      </div>
      <v-btn color="primary" @click="download">{{ i18n.btn_download }}</v-btn>
      <subjectslist :meetData="app.meet" :i18nData="i18n"></subjectslist>
      <attendantslist :meetData="app.meet" :i18nData="i18n"></attendantslist>
      <fileslist :meetData="app.meet" :i18nData="i18n"></fileslist>
      <noteslist :meetData="app.meet" :i18nData="i18n"></noteslist>
      <attendantsrates :meetData="app.meet" :i18nData="i18n"></attendantsrates>
    </v-content>
    <appfooter></appfooter>
    <apperrortoast :message="errorDialogMessage" :showErrorDialog="showErrorDialog" :i18n="i18n"></apperrortoast>
  </v-app>
</template>

<script>
import moment from 'moment'
import AppToolbar from '@/components/AppToolbar'
import AppFooter from '@/components/AppFooter'
import AppErrorToast from '@/components/AppErrorToast'
import MeetAttendantsList from '@/components/MeetAttendantsList'
import MeetSubjectsList from '@/components/MeetSubjectsList'
import MeetFilesList from '@/components/MeetFilesList'
import MeetSubjectNotesList from '@/components/MeetSubjectNotesList'
import MeetAttendantsRates from '@/components/MeetAttendantsRates'
import Meeting from '@/model/meeting'

const appData = window.appData || {}
export default {
  name: 'MeetInfo',
  components: {
    'apptoolbar': AppToolbar,
    'appfooter': AppFooter,
    'apperrortoast': AppErrorToast,
    'attendantslist': MeetAttendantsList,
    'subjectslist': MeetSubjectsList,
    'fileslist': MeetFilesList,
    'noteslist': MeetSubjectNotesList,
    'attendantsrates': MeetAttendantsRates
  },
  computed: {
    myrate: function () {
      return this.app.meet.attendants.find((attd) => { return attd.user_id.user_id === this.user.user_id })
    },
    rate: function () {
      let rates = this.app.meet.attendants.filter((attd) => { return attd.attd_rate != null })
      let ratesum = 0
      rates.forEach((r) => { ratesum += r.attd_rate })
      return rates.length > 0 ? Math.round(ratesum / rates.length) : 0
    }
  },

  data () {
    return {
      notificationsPanel: true,
      errorDialogMessage: '',
      showErrorDialog: false,
      showRateDialog: true,
      app: Object.assign(appData.app, {
        meet: new Meeting(appData.app.meet)
      }),
      user: Object.assign({
      }, appData.user),
      i18n: Object.assign({
        btn_close: 'Close',
        btn_download: 'Download',
        btn_rate: 'Rate',
        label_attd_comment: 'Comment',
        label_meet_time_start: 'Begin date',
        label_meet_duration: 'Duration',
        title_rate: 'Rate meeting'
      }, appData.i18n)
    }
  },

  methods: {
    _evtRequestError: function (errorResponse) {
      let errdata = errorResponse.response.data
      let srverr = errdata.srverr

      if (srverr) {
        this.errorDialogMessage = srverr.error_message
      } else {
        this.errorDialogMessage = this.i18n.error_unhandled
      }

      this.showErrorDialog = false
      this.showErrorDialog = true
    },
    download: function () {
      window.open(this.app.meet.getURL('/app/meet/{meet_id}/minute', this.app.meet))
    },
    getIconStar: function (range, value) {
      let icon

      if (value && value >= range) {
        icon = 'star'
      } else if (value && value >= range - 0.5) {
        icon = 'star_half'
      } else {
        icon = 'star_border'
      }

      return icon
    },
    parseDate: function (isodate) {
      return moment(isodate).format(this.user.locale.timestampformat)
    },
    cancelRate: function () {
      this.myrate.attd_rate = null
      this.myrate.attd_comment = null
      this.showRateDialog = false
    },
    saveRate: function () {
      this.myrate.save().then(() => {
        this.showRateDialog = false
      })
    }
  }
}
</script>

<style scoped>

</style>

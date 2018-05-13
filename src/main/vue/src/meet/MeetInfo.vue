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
      <v-container fluid grid-list-lg style="max-width: 600px;">
        <v-layout row wrap>
          <v-flex xs12>
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
                <v-dialog v-if="myrate != null" v-model="showRateDialog" persistent max-width="500px">
                  <a slot="activator" >{{ i18n.btn_rate }}</a>
                  <v-form v-model="validRateForm" ref="rateForm">
                    <v-card>
                      <v-card-title>
                        <span class="headline">{{  i18n.title_rate}}</span>
                      </v-card-title>
                      <v-card-text>
                        <v-layout row wrap>
                          <v-flex xs12>
                            <v-btn flat icon color="warning" class="ma-0" @click="setRate(myrate, 1)">
                              <v-icon>{{ getIconStar(1, myrate.attd_rate) }}</v-icon>
                            </v-btn>
                            <v-btn flat icon color="warning" class="ma-0" @click="setRate(myrate, 2)">
                              <v-icon>{{ getIconStar(2, myrate.attd_rate) }}</v-icon>
                            </v-btn>
                            <v-btn flat icon color="warning" class="ma-0" @click="setRate(myrate, 3)">
                              <v-icon>{{ getIconStar(3, myrate.attd_rate) }}</v-icon>
                            </v-btn>
                            <v-btn flat icon color="warning" class="ma-0" @click="setRate(myrate, 4)">
                              <v-icon>{{ getIconStar(4, myrate.attd_rate) }}</v-icon>
                            </v-btn>
                            <v-btn flat icon color="warning" class="ma-0" @click="setRate(myrate, 5)">
                              <v-icon>{{ getIconStar(5, myrate.attd_rate) }}</v-icon>
                            </v-btn>
                          </v-flex>
                          <v-flex xs12>
                            <v-text-field
                              :label="i18n.label_attd_comment"
                              v-model="myrate.attd_comment"
                              required
                              :rules="[() => myrate.attd_comment != null && myrate.attd_comment.length > 0 || i18n.rule_required]"
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
                  </v-form>
                </v-dialog>
              </v-flex>
            </v-layout>
          </v-flex>
          <v-flex xs12>
            <p>{{ app.meet.meet_description }}</p>
            <div>
              <div>{{ i18n.label_meet_time_start }}: {{ parseDate(app.meet.meet_time_start) }}</div>
              <div>{{ i18n.label_meet_duration }}: {{ app.meet.meet_duration }}</div>
            </div>
            <v-btn color="primary" @click="download">{{ i18n.btn_download }}</v-btn>
          </v-flex>
          <v-flex xs12>
            <subjectslist :meetData="app.meet" :i18nData="i18n" :isLeader="isLeader"></subjectslist>
          </v-flex>
          <v-flex xs12>
            <attendantslist :meetData="app.meet" :i18nData="i18n" :isLeader="isLeader"></attendantslist>
          </v-flex>
          <!--<v-flex xs12>-->
            <!--<fileslist :meetData="app.meet" :i18nData="i18n"></fileslist>-->
          <!--</v-flex>-->
          <v-flex xs12>
            <noteslist :meetData="app.meet" :i18nData="i18n" :isLeader="isLeader"></noteslist>
          </v-flex>
          <v-flex xs12>
            <attendantsrates :meetData="app.meet" :i18nData="i18n" :isLeader="isLeader"></attendantsrates>
          </v-flex>
        </v-layout>
      </v-container>
    </v-content>
    <appfooter></appfooter>
  </v-app>
</template>

<script>
import moment from 'moment'
import AppToolbar from '@/components/AppToolbar'
import AppFooter from '@/components/AppFooter'
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
    'attendantslist': MeetAttendantsList,
    'subjectslist': MeetSubjectsList,
    'fileslist': MeetFilesList,
    'noteslist': MeetSubjectNotesList,
    'attendantsrates': MeetAttendantsRates
  },
  computed: {
    isLeader: function () {
      return this.app.meet.meet_leader.user_id === this.user.user_id
    },
    myrate: function () {
      return this.app.meet.attendants.toJSON().find((attd) => { return attd.user_id.user_id === this.user.user_id })
    },
    rate: function () {
      let rates = this.app.meet.attendants.toJSON().filter((attd) => { return attd.attd_rate != null })
      let ratesum = 0
      rates.forEach((r) => { ratesum += r.attd_rate })
      return rates.length > 0 ? Math.round(ratesum / rates.length) : 0
    }
  },

  data () {
    return {
      notificationsPanel: true,
      showRateDialog: false,
      app: Object.assign(appData.app, {
        meet: this.$getModelInstance(Meeting, appData.app.meet)
      }),
      user: Object.assign({
      }, appData.user),
      validRateForm: true,
      i18n: Object.assign({
        btn_close: 'Close',
        btn_download: 'Download',
        btn_rate: 'Rate',
        label_attd_comment: 'Comment',
        label_meet_time_start: 'Begin date',
        label_meet_duration: 'Duration',
        title_rate: 'Rate meeting',
        rule_required: 'This field is required'
      }, appData.i18n)
    }
  },

  created: function () {
    this.$initWebsocket('/entity', () => {
      this.$socket.onmessage = (e) => {
        var data = JSON.parse(e.data)
        if (data.message === 'observe' && data.success === true) {
          // DO NOTHING
        } else if (data.message === 'update' && data.entity === 'meeting' && data.key === this.app.meet.meet_id) {
          if (!this.isLeader) {
            this.app.meet.fetch()
          } else {
            this.$saveFetch(this.app.meet)
          }
        }
      }

      this.$socket.sendObj({ message: 'observe', entity: 'meeting', key: this.app.meet.meet_id })
    })
  },

  methods: {
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
    setRate: function (attd, ratevalue) {
      if (ratevalue === 1) {
        attd.attd_rate = attd.attd_rate === 1 ? 0 : 1
      } else {
        attd.attd_rate = ratevalue
      }
    },
    saveRate: function () {
      if (this.$refs.rateForm.validate()) {
        this.myrate.save().then(() => {
          this.showRateDialog = false
        })
      }
    }
  }
}
</script>

<style scoped>

</style>

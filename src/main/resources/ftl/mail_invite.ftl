<div style="font-family: arial,sans-serif; font-size: 13px; background: #FFFFFF; box-shadow: 0 1px 2px rgba(0,0,0,0.075); border: 1px solid #d6d6d6; border-radius: 5px; margin: 7px 15px 14px 30px; padding: 7px 15px 14px 30px;">
	<table cellpadding="0">
			<tbody>
				<tr>
						<td colspan="2"><h3>${meet.getMeetTitle()}</h3></td>
				</tr>
				<tr>
					<td style="vertical-align: text-bottom; color: #999999">${i18n.label_when}</td>
					<td style="vertical-align: text-bottom;">${meet.getMeetDate()} ${meet.getMeetTimeStart()} -${meet.getMeetTimeEnd()} </td>
				</tr>
				<tr>
						<td style="vertical-align: text-bottom; color: #999999">${i18n.label_attendants}</td>
						<td style="vertical-align: text-bottom;">
								<ul style="padding: 0">
										<li>${meet.getMeetOrganizer()} - ${i18n.label_organizer}</li>
										<#list meet.getMeetAttendants() as attendant>
										<li>${attendant}</li>
										</#list>
								</ul>
						</td>
				</tr>
				<tr>
						<td colspan="2"><a href="${meet.getMeetURL()}">${i18n.label_goto}</a></td>
				</tr>
		</tbody>
	</table>
</div>
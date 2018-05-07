<?xml version="1.0"?>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<fo:layout-master-set>
		<fo:simple-page-master master-name="content"
							   page-width="210mm" page-height="297mm">
			<fo:region-body/>
		</fo:simple-page-master>
	</fo:layout-master-set>
	<fo:page-sequence master-reference="content">
		<fo:flow flow-name="xsl-region-body">
			<fo:table table-layout="fixed" width="100%">
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell display-align="center">
							<fo:block text-align="center">
								<fo:table table-layout="fixed" width="100%">
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-body>
										<fo:table-row>
											<fo:table-cell display-align="center" number-columns-spanned="3">
												<fo:block text-align="center">${i18n.title_meeting}</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row>
											<fo:table-cell display-align="center" number-columns-spanned="3">
												<fo:block text-align="center">${i18n.label_reason}: ${meet.getMeetTitle()}</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row>
											<fo:table-cell display-align="center">
												<fo:block text-align="left">${i18n.label_meet_date}: ${meet.getMeetDate()}</fo:block>
											</fo:table-cell>
											<fo:table-cell display-align="center">
												<fo:block text-align="left">${i18n.label_meet_begin}: ${meet.getMeetTimeStart()}</fo:block>
											</fo:table-cell>
											<fo:table-cell display-align="center">
												<fo:block text-align="left">${i18n.label_meet_end}: ${meet.getMeetTimeEnd()}</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell display-align="center">
							<fo:block text-align="center">
								Attendants
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:flow>
	</fo:page-sequence>
</fo:root>
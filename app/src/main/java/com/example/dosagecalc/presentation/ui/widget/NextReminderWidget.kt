package com.example.dosagecalc.presentation.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.dosagecalc.MainActivity
import com.example.dosagecalc.domain.model.Reminder

class NextReminderWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val nextReminder = WidgetDataProvider.getNextReminder(context)
        provideContent { WidgetContent(nextReminder) }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun WidgetContent(reminder: Reminder?) {
        val bgColor = Color(0xFF148F84)

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(bgColor)
                .cornerRadius(20.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.TopStart
        ) {
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = GlanceModifier
                        .size(90.dp)
                        .background(Color.White.copy(alpha = 0.07f))
                        .cornerRadius(45.dp)
                ) {}
            }

            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {
                Box(
                    modifier = GlanceModifier
                        .size(56.dp)
                        .background(Color.White.copy(alpha = 0.05f))
                        .cornerRadius(28.dp)
                ) {}
            }

            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Text(
                        text = "PROSSIMO REMINDER",
                        modifier = GlanceModifier.defaultWeight(),
                        style = TextStyle(
                            color = ColorProvider(Color.White.copy(alpha = 0.70f)),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Box(
                        modifier = GlanceModifier
                            .size(28.dp)
                            .background(Color.White.copy(alpha = 0.14f))
                            .cornerRadius(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "R",
                            style = TextStyle(
                                color = ColorProvider(Color.White.copy(alpha = 0.80f)),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(10.dp))

                if (reminder != null) {
                    Text(
                        text = String.format(java.util.Locale.US, "%02d:%02d", reminder.hour, reminder.minute),
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(3.dp))
                    Text(
                        text = reminder.drugName,
                        maxLines = 1,
                        style = TextStyle(
                            color = ColorProvider(Color.White.copy(alpha = 0.88f)),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Text(
                        text = reminder.interval.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = TextStyle(
                            color = ColorProvider(Color.White.copy(alpha = 0.55f)),
                            fontSize = 10.sp
                        )
                    )
                } else {
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Text(
                        text = "Nessun promemoria",
                        style = TextStyle(
                            color = ColorProvider(Color.White.copy(alpha = 0.50f)),
                            fontSize = 13.sp
                        )
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                }
            }
        }
    }
}

class NextReminderWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NextReminderWidget()
}

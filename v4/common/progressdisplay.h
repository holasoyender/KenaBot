// =================
// progressdisplay.h
// =================

#ifndef _COMMON_PROGRESSDISPLAY_H_
#define _COMMON_PROGRESSDISPLAY_H_

#include "common.h"
#include <time.h>

class ProgressDisplay
{
public:
	ProgressDisplay() : m_progress(0.0f), m_numChars(0), m_clock(0) {}
	ProgressDisplay(const char* format, ...) : m_progress(0.0f), m_numChars(0), m_clock(0)
	{
		char temp[1024] = "";
		va_list args;
		va_start(args, format);
		vsnprintf(temp, sizeof(temp), format, args);
		va_end(args);

		sprintf(m_string, "%s .. ", temp);
		fprintf(stdout, m_string);
		fflush(stdout);

		Update(-1, 1);

		m_clock = clock();

		m_remainingTimeEnabled = true; // TODO -- fix this, it's not quite working right
		m_remainingTime = 0;
		m_remainingTimeStr[0] = '\0';
	}

	void Update(float f)
	{
		m_progress = f;
		char temp[64] = "";
		sprintf(temp, "%.3f%%", 100.0f*m_progress);
		Erase(m_numChars);
		fprintf(stdout, "%s", temp);
		fflush(stdout);
		m_numChars = (int)strlen(temp);

		if (m_remainingTimeEnabled)
		{
			const float elapsed = (float)(clock() - m_clock)/(float)CLOCKS_PER_SEC;

			if (elapsed > 5.0f && m_progress > 0.0f)
			{
				if (clock() - m_remainingTime > CLOCKS_PER_SEC)
				{
					const float secs = elapsed*(1.0f - m_progress)/m_progress;
					const float mins = secs/60.0f;
					const float hrs  = mins/60.0f;

					if      (secs <= 100.0f) { sprintf(m_remainingTimeStr, " (%.2f secs remaining)", secs); }
					else if (mins <= 100.0f) { sprintf(m_remainingTimeStr, " (%.2f mins remaining)", mins); }
					else                     { sprintf(m_remainingTimeStr, " (%.2f hours remaining)", hrs); }

					m_remainingTime = clock();
				}

				fprintf(stdout, m_remainingTimeStr);
				fflush(stdout);
				m_numChars += (int)strlen(m_remainingTimeStr);
			}
		}
	}

	void Update(int i, int n)
	{
		Update((float)(i + 1)/(float)n);
	}

	template <typename T> void Update(int i, const T& v)
	{
		Update(i, (int)v.size());
	}

	void Report(const char* format, ...)
	{
		Erase(m_numChars + (int)strlen(m_string));
		m_numChars = 0;

		char temp[1024] = "";
		va_list args;
		va_start(args, format);
		vsnprintf(temp, sizeof(temp), format, args);
		va_end(args);

		fprintf(stdout, "%s%s", temp, m_string);
		fflush(stdout);

		Update(m_progress);
	}

	void End(const char* format = "done", ...)
	{
		Erase(m_numChars);

		char temp[1024] = "";
		va_list args;
		va_start(args, format);
		vsnprintf(temp, sizeof(temp), format, args);
		va_end(args);

		fprintf(stdout, "%s.", temp);

		//if (bShowTime)
		{
			char totalStr[64] = "";

			if (1) // show total
			{
				static clock_t total = 0;

				total += clock() - m_clock;

				const float totalSecs = (float)total/(float)CLOCKS_PER_SEC;
				const float totalMins = totalSecs/60.0f;
				const float totalHrs  = totalMins/60.0f;

				if      (totalSecs <=   1.0f) { sprintf(totalStr, ""); }
				else if (totalSecs <= 100.0f) { sprintf(totalStr, ", total %.2f secs", totalSecs); }
				else if (totalMins <= 100.0f) { sprintf(totalStr, ", total %.2f mins", totalMins); }
				else                          { sprintf(totalStr, ", total %.2f hours", totalHrs); }
			}

			const float secs = (float)(clock() - m_clock)/(float)CLOCKS_PER_SEC;
			const float mins = secs/60.0f;
			const float hrs  = mins/60.0f;

			if      (secs <=   1.0f) { fprintf(stdout, ""); }
			else if (secs <= 100.0f) { fprintf(stdout, " (%.2f secs%s)", secs, totalStr); }
			else if (mins <= 100.0f) { fprintf(stdout, " (%.2f mins%s)", mins, totalStr); }
			else                     { fprintf(stdout, " (%.2f hours%s)", hrs, totalStr); }
		}

		if (m_remainingTimeEnabled)
		{
			fprintf(stdout, "                             \n"); // extra space to clear leftover chars
		}
		else
		{
			fprintf(stdout, "    \n"); // extra space to clear leftover chars
		}
	}

protected:
	static void Erase(int numChars)
	{
		for (int k = 0; k < numChars; k++)
		{
			fprintf(stdout, "%c", 0x08);
		}

		fflush(stdout);
	}

	char    m_string[512];
	float   m_progress;
	int     m_numChars;
	clock_t m_clock;

	bool    m_remainingTimeEnabled;
	clock_t m_remainingTime;
	char    m_remainingTimeStr[64];
};

class ProgressDisplayAuto : public ProgressDisplay
{
public:
	ProgressDisplayAuto(const char* format, ...)
	{
		char temp[1024] = "";
		va_list args;
		va_start(args, format);
		vsnprintf(temp, sizeof(temp), format, args);
		va_end(args);

		fprintf(stdout, "%s .. ", temp);
		fflush(stdout);

		//Update(-1, 1);

		m_clock = clock();
	}

	~ProgressDisplayAuto()
	{
		End();
	}
};

#endif // _COMMON_PROGRESSDISPLAY_H_

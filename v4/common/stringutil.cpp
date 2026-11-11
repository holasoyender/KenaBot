// ==============
// stringutil.cpp
// ==============

#include "stringutil.h"

void strcpy_ext(char* dst, const char* src, const char* ext)
{
	strcpy(dst, src);
	char* s = strrchr(dst, '.');

	if (s) { strcpy(s, ext); }
	else   { strcat(s, ext); }
}

const char* stristr(const char* str, const char* substr, const char* nomatch)
{
	const int len = (int)strlen(substr);

	for (const char* s = str; *s; s++)
	{
		if (_strnicmp(s, substr, len) == 0)
		{
			return s;
		}
	}

	return nomatch;
}

char* stristr(char* str, const char* substr, char* nomatch)
{
	const int len = (int)strlen(substr);

	for (char* s = str; *s; s++)
	{
		if (_strnicmp(s, substr, len) == 0)
		{
			return s;
		}
	}

	return nomatch;
}

const char* strskip(const char* str, const char* skip)
{
	const char* s = str;
	while (*s && *s == *skip) { s++; skip++; }
	return (*skip) ? str : s;
}

char* strskip(char* str, const char* skip)
{
	char* s = str;
	while (*s && *s == *skip) { s++; skip++; }
	return (*skip) ? str : s;
}

const char* strsearch(const char* str, const char* search)
{
	const char* s = strstr(str, search);
	return s ? strskip(s, search) : NULL;
}

char* strsearch(char* str, const char* search)
{
	char* s = strstr(str, search);
	return s ? strskip(s, search) : NULL;
}

bool if_strskip(const char*& str, const char* skip)
{
	const char* s = strskip(str, skip);

	if (s != str)
	{
		str = s;
		return true;
	}
	else
	{
		return false;
	}
}

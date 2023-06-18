#include <iostream>
#include <fstream>
#include <stdio.h>
#include <string>
#include <ctime>
#include <sstream>
#include <bits/stdc++.h>
#include <vector>

using namespace std;

void returnNotSupportedError(string command)
{
  cout << "Oops, command '" << command << "' is not supported!, try again or type 'help'" << endl;
}
vector<string> tokenizeString(string str, char delimiter)
{
  stringstream ss(str);
  vector<string> tokens;
  string token;

  while (getline(ss, token, delimiter))
  {
    tokens.push_back(token);
  }
  return tokens;
}

void filterList(vector<string> lines, string filterQuery)
{
  string command1 = "list ";

  if (filterQuery.compare("locations") == 0)
  {
    set<string> items;
    for (string line : lines)
    {
      vector<string> tokens = tokenizeString(line, ',');
      items.insert(tokens.at(0));
    }
    for (auto item : items)
    {
      cout << item << endl;
    }
  }
  else if (filterQuery.compare("diseases") == 0)
  {
    set<string> items;
    for (string line : lines)
    {
      vector<string> tokens = tokenizeString(line, ',');
      items.insert(tokens.at(1));
    }
    for (auto item : items)
    {
      cout << item << endl;
    }
  }
  else
  {
    returnNotSupportedError(command1.append(filterQuery));
  }
}

bool isDuplicate(string location)
{
  ifstream file("database.csv");
  string line;
  bool found = false;

  while (getline(file, line))
  {
    if (line.find(location) != string::npos)
    {
      found = true;
      break;
    }
  }
  return found;
  file.close();
}

void returnList(string type, string filename)
{
  ifstream File(filename);
  // temp variable to store each line
  string line;
  vector<string> lines;
  if (!File.is_open())
  {
    // no file found hence no data recorded so far!
    cout << "No " << type << "found!" << endl;
    return;
  }
  while (getline(File, line))
  {
    if (line.empty())
    {
      cout << "No " << type << "found!" << endl;
      return;
    }
    else
    {
      lines.push_back(line);
    }
  }
  // (Delimiter is ',' in .csv file)
  filterList(lines, type);
}

void addLocation(string location)
{

  if (isDuplicate(location))
  {
    cout << "Location " << location << " already exists" << endl;
    return;
  }
  string filename = "database.csv";
  ofstream outputFile(filename, ios::app); // Open the file in append mode

  string loc = location;
  string newData = location.append(",null,null");
  outputFile << newData << endl;

  outputFile.close();

  cout << "Location " << loc << " is successfully added!" << endl;
}

void record(vector<string> commands, string command)
{
  if (commands.size() != 4)
  {
    returnNotSupportedError(command);
    return;
  }
  else
  {
    string location = commands.at(1);
    string disease = commands.at(2);
    string cases = commands.at(3);
    string line;
    vector<string> lines;

    string filename = "database.csv";
    ifstream inputFile(filename);
    while (getline(inputFile, line))
    {
      if (line.find(location) != string::npos)
      {
        continue;
      }
      lines.push_back(line);
    }
    lines.push_back(location.append(",").append(disease).append(",").append(cases));
    inputFile.close();

    ofstream outputFile(filename);
    for (auto line : lines)
    {
      outputFile << line << endl;
    }
    outputFile.close();
    cout << "Data successfully recorded!" << endl;
  }
}
void deleteRecord(string location)
{
  if (!isDuplicate(location))
  {
    cout << "Location: " << location << " doesn't exists!" << endl;
  }
  else
  {
    string line;
    vector<string> lines;

    string filename = "database.csv";
    ifstream inputFile(filename);
    while (getline(inputFile, line))
    {
      if (line.find(location) != string::npos)
      {
        continue;
      }
      lines.push_back(line);
    }
    inputFile.close();

    ofstream outputFile(filename);
    for (auto line : lines)
    {
      outputFile << line << endl;
    }
    outputFile.close();
    cout << "Data associated with location " << location << " successfully deleted!" << endl;
  }
}

void returnLocationsWithDiseases(string disease)
{
  string line;
  vector<string> locations;

  string filename = "database.csv";
  ifstream inputFile(filename);

  while (getline(inputFile, line))
  {
    if (line.find(disease) != string::npos)
    {
      vector<string> tokens = tokenizeString(line, ',');
      locations.push_back(tokens.at(0));
    }
  }

  // print out the locations
  cout << "[";
  for (auto location : locations)
  {
    cout << location;
  }
  cout << "]\n";

  inputFile.close();
}

void returnCasesInLocation(vector<string> commands)
{
  string line;
  string location = commands.at(1);
  string disease = commands.at(2);
  int numOfCases = 0;

  string filename = "database.csv";
  ifstream inputFile(filename);

  while (getline(inputFile, line))
  {
    if (line.find(location) != string::npos)
    {
      vector<string> tokens = tokenizeString(line, ',');
      numOfCases += stoi(tokens.at(2));
    }
  }

  cout << "Cases of " << disease << " at " << location << " are: " << numOfCases << endl;
}
void returnTotalCases(vector<string> commands)
{
  string line;
  string disease = commands.at(1);
  int numOfCases = 0;

  string filename = "database.csv";
  ifstream inputFile(filename);

  while (getline(inputFile, line))
  {
    vector<string> tokens = tokenizeString(line, ',');
    numOfCases += stoi(tokens.at(2));
  }

  cout << "Total cases of '" << disease << "' = " << numOfCases << endl;
}
void help()
{
  cout << "Need help? Type 'help' command then press Enter key." << endl;
  string command;

  do
  {
    cout << "\nConsole > ";
    getline(cin, command);
    transform(command.begin(), command.end(), command.begin(), ::tolower);

    vector<string> tokens = tokenizeString(command, ' ');

    if (tokens.size() == 1 && tokens.at(0).compare("help") == 0)
    {
      cout << "================================================================" << endl;
      cout << "*\t\t\t"
           << "HELP MENU"
           << "\t\t\t" << endl;
      cout << "================================================================" << endl;
      cout << "add <Location>"
           << "\t\t\t\t"
           << ":Add a new location" << endl;
      cout << "delete <Location>"
           << "\t\t\t"
           << ":Delete an existing location" << endl;
      cout << "record <Location> <disease> <cases>"
           << "\t"
           << ":Record a disease and its cases" << endl;
      cout << "list locations"
           << "\t\t\t\t"
           << ":List all existing locations" << endl;
      cout << "list diseases"
           << "\t\t\t\t"
           << ":List existing Diseases in locations" << endl;
      cout << "where <disease>"
           << "\t\t\t\t"
           << ":Find where disease exists" << endl;
      cout << "cases <location><disease>"
           << "\t\t"
           << ":Find cases of a disease in location" << endl;
      cout << "cases <disease>"
           << "\t\t\t\t"
           << ":Find total cases of a given disease" << endl;
      cout << "help"
           << "\t\t\t\t\t"
           << ":Prints user manual" << endl;
      cout << "Exit"
           << "\t\t\t\t\t"
           << ":Exit the program" << endl;
    }
    else if (tokens.at(0).compare("list") == 0)
    {
      returnList(tokens.at(1), "database.csv");
    }
    else if (tokens.at(0).compare("add") == 0)
    {
      addLocation(tokens.at(1));
    }
    else if (tokens.at(0).compare("record") == 0)
    {
      record(tokens, command);
    }
    else if (tokens.at(0).compare("delete") == 0)
    {
      deleteRecord(tokens.at(1));
    }
    else if (tokens.at(0).compare("where") == 0)
    {
      returnLocationsWithDiseases(tokens.at(1));
    }
    else if (tokens.at(0).compare("cases") == 0)
    {
      if (tokens.size() == 3)
      {
        returnCasesInLocation(tokens);
      }
      else if (tokens.size() == 2)
      {
        returnTotalCases(tokens);
      }
      else
      {
        returnNotSupportedError(command);
      }
    }
    else if (command.compare("exit") == 0)
    {
      cout << "BYE!" << endl;
      break;
    }
    else
    {
      returnNotSupportedError(command);
    }
  } while (command.compare("exit") != 0);
}

int main()
{
  time_t currentTime = time(NULL);
  string time = ctime(&currentTime);

  cout << "=============================================" << endl;
  cout << "*\t"
       << "Welcome to Disease Cases Reporting System!"
       << "\t*" << endl;
  cout << "* "
       << "*****************************************"
       << " *" << endl;
  cout << "*\t\t\t\t*" << endl;
  cout << "* "
       << "It is developed by Aime Igirimpuhwe as practical"
       << " *" << endl;
  cout << "* "
       << "evaluation for the end of Year 3."
       << "\t\t*" << endl;
  cout << "=============================================" << endl;

  cout << "Starting time: " << time << endl;

  help();

  return 0;
}
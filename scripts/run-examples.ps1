# Runs every single-file example under lessons/*/examples. Fails on the first error. The Windows (PowerShell) twin of run-examples.sh.
# Works in Windows PowerShell 5.1 and in PowerShell 7 (Windows, Linux, macOS).
#
# Lessons 25-32 and 38 talk to the H2 database, so the H2 jar is put on the class path for every example (harmless for the others).
# The jar comes from your Maven cache (~/.m2). If it is not there yet it is downloaded once with the Maven wrapper.
# Lesson 35 also needs Jackson: a lesson whose examples folder contains the marker file .needs-solution-classpath gets the
# class path of its solution module (the libraries it depends on) from Maven instead. The first run downloads them.
# To use another copy of H2:   $env:H2_JAR = 'C:\path\to\h2.jar'; .\scripts\run-examples.ps1
#
# If PowerShell refuses to run scripts:   powershell -ExecutionPolicy Bypass -File scripts\run-examples.ps1
$ErrorActionPreference = 'Stop'
Set-Location (Join-Path $PSScriptRoot '..')

$onWindows = ($PSVersionTable.PSVersion.Major -lt 6) -or $IsWindows
$mvnw = if ($onWindows) { '.\mvnw.cmd' } else { './mvnw' }

$pom = Get-Content -Raw -Path 'pom.xml'
$h2Version = [regex]::Match($pom, '<h2.version>(.*?)</h2.version>').Groups[1].Value
if ($env:H2_JAR) { $h2Jar = $env:H2_JAR } else { $h2Jar = Join-Path $HOME ".m2/repository/com/h2database/h2/$h2Version/h2-$h2Version.jar" }
if (-not (Test-Path $h2Jar)) {
    Write-Host "Downloading H2 $h2Version ..."
    & $mvnw -B -q dependency:get "-Dartifact=com.h2database:h2:$h2Version"
    if ($LASTEXITCODE -ne 0) { throw "Could not download H2" }
}

$count = 0
foreach ($example in Get-ChildItem -Path 'lessons/*/examples/*.java' | Sort-Object FullName) {
    $dir = $example.DirectoryName
    $classpath = $h2Jar
    if (Test-Path (Join-Path $dir '.needs-solution-classpath')) {
        $lesson = Split-Path -Parent $dir
        $out = Join-Path $lesson 'solution/target/example-classpath.txt'
        if (-not (Test-Path $out)) {
            # the output file is relative to the module, as in the shell script
            $relative = 'lessons/' + (Split-Path -Leaf $lesson) + '/solution'
            & $mvnw -B -q -pl $relative dependency:build-classpath '-Dmdep.outputFile=target/example-classpath.txt'
            if ($LASTEXITCODE -ne 0) { throw "Could not build the class path of $lesson" }
        }
        $classpath = (Get-Content -Raw -Path $out).Trim()
    }
    Write-Host "== $($example.FullName.Substring((Get-Location).Path.Length + 1))"
    & java -cp $classpath $example.FullName
    if ($LASTEXITCODE -ne 0) { throw "Example failed: $($example.FullName)" }
    $count++
}
Write-Host "OK: $count examples ran"

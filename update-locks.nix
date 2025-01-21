{
  writeShellScriptBin,
  gradle,
  yq,
}:
writeShellScriptBin "update-locks" ''
  set -eu -o pipefail
  ${gradle}/bin/gradle dependencies --write-locks
  ${gradle}/bin/gradle --write-verification-metadata sha256 dependencies
  ${yq}/bin/xq '
      ."verification-metadata".components.component |
      map({ group: ."@group", name: ."@name", version: ."@version",
        artifacts: [([.artifact] | flatten | .[] | {(."@name"): .sha256."@value"})] | add
      })
    ' gradle/verification-metadata.xml > deps.json
  rm gradle/verification-metadata.xml
''

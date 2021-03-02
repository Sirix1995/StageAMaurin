#!/usr/bin/perl -w


# Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


sub getMods
{
	my $opt = $_[0];
	if ($opt =~ /mods=\(((?:\w|\s)*)\)/)
	{
		$_ = " " . uc $1;
		s/\s\b/ $fieldType./g;
		s/\b\s+\b/ | /g;
	}
	else
	{
		$_ = " 0";
	}
	if ($opt =~ /\bnoembed/)
	{
		$_ .= " | $fieldType.UNMANAGED";
	}
	elsif (($opt =~ /\bfco/) || ($opt =~ /\bedge=/))
	{
		$_ .= " | $fieldType.FCO";
	}
	else
	{
		$_ .= " | $fieldType.SCO";
	}
	if ($opt =~ /\bprovide/)
	{
		$_ .= " | $fieldType.GETS_COPY";
	}
	if ($opt =~ /\bdefinesshared/)
	{
		$_ .= " | $fieldType.DEFINES_SHARED";
	}
	if ($opt =~ /\bhidden/)
	{
		$_ .= " | $fieldType.HIDDEN";
	}
	return $_;
}


sub getTypeId
{
	return ($_[0] eq "boolean") ? 0
		: ($_[0] eq "byte") ? 1
		: ($_[0] eq "short") ? 2
		: ($_[0] eq "char") ? 3
		: ($_[0] eq "int") ? 4
		: ($_[0] eq "long") ? 5
		: ($_[0] eq "float") ? 6
		: ($_[0] eq "double") ? 7 : 8;
}


sub wrapType
{
	my $id = getTypeId $_[0];
	return ($id == 8) ? $_[0] : "de.grogra.reflect.Type." . uc $_[0];
}


sub setType
{
	$typeId = $_[0];
	$type = $typeNames[$_[0]];
	$Type = ucfirst $type;
}


sub beginGeneratedCode
{
	print "//enh:begin\n// NOTE: The following lines up to enh:end were generated automatically\n\n";
}


sub endGeneratedCode
{
	print "//enh:end\n";
	$lookForBegin = 1;
}


sub printGetSet
{
	my $id = $_[0] ? ", int id" : "";
	my $mod = $_[0] ? "protected" : "public";
	for ($typeId = 0; $typeId < 9; $typeId++)
	{
		setType $typeId;
		my @list = @{$stack[$typeId]};
		if ($#list >= 0)
		{
			my $smName = "set$Type";
			my $smMod = $mod;
			if (($typeId == 8) && ($id eq ""))
			{
				$smName = "set${Type}Impl";
				$smMod = "protected";
			}
			print "\n\t\t\@Override";
			print "\n\t\t$smMod void $smName (Object o$id, $type value)\n\t\t{\n\t\t\tswitch (id)\n\t\t\t{\n";
			foreach (@list)
			{
				my $t = $$_[1];
				my $options = $$_[3];
				print "\t\t\t\tcase $$_[0]:\n\t\t\t\t\t";
				if ($options =~ /\bsetexpr=\{(.*?)\}/)
				{
					my $expr = $1;
					$expr =~ s/\$/(($class) o)/g;
					print $expr;
				}
				else
				{
					print "(($class) o).";
					if ($options =~ /\bsetmethod=(\S+)/)
					{
						print "$1 (($t) value)";
					}
					else
					{
						print "$$_[2]";
						if ($options =~ /\bset=(\S+)/)
						{
							print ".$1 (($t) value)";
						}
						elsif (($typeId == 8) && ($options =~ /\btype=(\S+)/))
						{
							print " = ($t) $1.setObject ((($class) o).$$_[2], value)";
						}
						else
						{
							print " = ($t) value";
						}
					}
				}
				print ";\n\t\t\t\t\treturn;\n";
			}
			print "\t\t\t}\n\t\t\tsuper.$smName (o" . ($id && ", id")
				. ", value);\n\t\t}\n";
			print "\n\t\t\@Override";
			print "\n\t\t$mod $type get$Type (Object o$id)\n\t\t{\n\t\t\tswitch (id)\n\t\t\t{\n";
			foreach (@list)
			{
				my $options = $$_[3];
				print "\t\t\t\tcase $$_[0]:\n\t\t\t\t\t";
				if ($options =~ /\bgetexpr=\{(.*?)\}/)
				{
					my $expr = $1;
					$expr =~ s/\$/(($class) o)/g;
					print "return $expr;\n";
				}
				elsif ($options =~ /\bgetmethod=(\S+)/)
				{
					my $get = $1;
					if ($options =~ /\bprovide\b/)
					{
						print "{$$_[1] t = new $$_[1] (); (($class) o).$get (t); return t;}\n";
					}
					else
					{
						print "return (($class) o).$get ();\n";
					}
				}
				elsif ($options =~ /\bgetter/)
				{
					print "return (($class) o)." . (($typeId == 0) ? "is" : "get") . ucfirst($$_[2]) . " ();\n";
				}
				else
				{
					print "return (($class) o).$$_[2];\n";
				}
			}
			print "\t\t\t}\n\t\t\treturn super.get$Type (o"
				. ($id && ", id") .");\n\t\t}\n";
		}
	}
}


sub printGetterSetter
{
	for ($typeId = 0; $typeId < 9; $typeId++)
	{
		setType $typeId;
		my @list = @{$stack[$typeId]};
		foreach (@list)
		{
			my $t = $$_[1];
			my $options = $$_[3];
			my $Name = ucfirst $$_[2];
			if ($options =~ /getter/)
			{
				print "\tpublic $t ";
				print (($typeId == 0) ? "is" : "get");
				print "$Name ()\n\t{\n\t\treturn $$_[2];\n\t}\n\n";
			}
			if ($options =~ /setter/)
			{
				print "\tpublic void set$Name ($t value)\n\t{\n\t\t";
				if ($typeId == 8)
				{
					print $$_[2] . "\$FIELD.setObject (this, value)";
				}
				else
				{
					print "this.$$_[2] = ($t) value";
				}
				print ";\n\t}\n\n";
			}
		}
	}
}


sub enhanceSCO
{
	my $file = $_[0];
	$file =~ s/\\/\//g; # remove backslashes to prevent \u unicode escapes
	$file =~ s/.*src\///g;
	print "// This file has been automatically generated\n// from $file.\n\n";
	open INPUT, $_[0];
	$header = 1;
	$footer = "";
	$_[0] =~ /(\w+)\.\w+$/;
	$scotype = $1; 
	$fieldCount = 0;
	$fieldType = "Field";
	while (<INPUT>)
	{
		if ($header)
		{
			if (/^begin/)
			{
				$header = 0;
				<INPUT> =~ /(.*)$/;
				$class = $1;
				<INPUT> =~ /(.*)$/;
				$supersco = $1;
				print "\npublic class $scotype extends $supersco\n{\n"
					. "\tpublic static final $scotype \$TYPE;\n\n";
				$staticInit = "\t\t\$TYPE = new $scotype ($class.class, $supersco.\$TYPE);\n";
			}
			else
			{
				print $_;
			}
		}
		elsif ($footer)
		{
			$footer .= $_;
		}
		elsif (/^end/)
		{
			$footer = "\n";
		}
		elsif (/>(.*)/)
		{
			print "$1\n";
		}
		elsif (/(\S+)\s+(\w+)((?:\s+.*)?)/)
		{
			$ftype = $1;
			$name = $2;
			$options = $3;
			$typeId = getTypeId ($ftype);
			setType $typeId;
			my $fc = $supersco . ".FIELD_COUNT + " . $fieldCount++;
			push @{$stack[$typeId]}, [$fc, $ftype, $name, $options];
			print "\tpublic static final $fieldType ${name}\$FIELD;\n";
			$staticInit .= "\t\t${name}\$FIELD = \$TYPE.addManagedField (\"$name\", "
				. getMods ($options) . ", "
				. (($options =~ /\btype=(\S+)/) ? $1
				   : ($typeId == 8) ? $ftype . ".class"
				   : wrapType ($ftype))
				. ", null, " . $fc . ");\n";
			if ($options =~ /quantity=(\S+)/)
			{
				$q = $1;
				($q =~ /\./) || ($q = "de.grogra.util.Quantity.$q");
				$staticInit .= "\t\t${name}\$FIELD.setQuantity ($q);\n";
			}
			if ($options =~ /min=(\S+)/)
			{
				$staticInit .= "\t\t${name}\$FIELD.setMinValue (new $wrapperNames[$typeId] ($1));\n";
			}
			if ($options =~ /max=(\S+)/)
			{
				$staticInit .= "\t\t${name}\$FIELD.setMaxValue (new $wrapperNames[$typeId] ($1));\n";
			}
		}
	}
	close INPUT;
	print "\n\tstatic\n\t{\n$staticInit\t\t\$TYPE.validate ();\n\t}\n\n";
	print "\tpublic $scotype (Class c, de.grogra.persistence.SCOType supertype)\n\t{\n\t\tsuper (c, supertype);\n\t}\n\n";
	print "\tpublic $scotype ($class representative, de.grogra.persistence.SCOType supertype)\n\t{\n\t\tsuper (representative, supertype);\n\t}\n\n";
	print "\tprotected static final int FIELD_COUNT = $supersco.FIELD_COUNT + $fieldCount;\n";
	printGetSet (1);
	print $footer . "}\n";
}


sub enhanceJava
{
	open INPUT, $_[0];

	$header = "";
	$class = "";
	$superclass = "";
	$lookForBegin = 0;
	$fieldType = "_Field";
	$sco = 0;

	my $fieldCount = 0;
	my $removeGeneratedCode = 0;
	my $pctype = "NType";
	my $superFieldType = $pctype . ".Field";
	my $fieldInit = "";
	my $attrDecl = "";
	my $fieldDecl = "";
	my $bitfieldGetterSetter = "";
	my $superscoclass = "";
	my $inbody = 0;
	my $body = "";
	while (<INPUT>)
	{
		$line = $_;
		if ($lookForBegin)
		{
			if (/^\s*\/\/\s*enh:begin/)
			{
				$removeGeneratedCode = 1;
			}
			$lookForBegin = 0;
		}
		print $_ if (!$removeGeneratedCode);
		if (/^\s*\*/)
		{
		}
		elsif ($superclass eq "")
		{
			$header .= $_;
			if ($header =~ /(.*?)\sclass\s+(\S+)/)
			{
				$class = $2;
				$abstract = $1;
				$generateNewInstance = ($1 !~ /abstract|noinstance/);
				$abstract = ($abstract =~ /abstract/);
				if ($class =~ /(\S+)<\S+>/)
				{
					$class = $1;
				}
			}
			if ($header =~ /.*?\sextends\s+(\S+)/)
			{
				$superclass = $1;
				if ($superclass =~ /(\S+)<\S+>/)
				{
					$superclass = $1;
				}
			}
		}
		elsif (/^\s*\/\/\s*enh:(.+)/)
		{
			$_ = $1;
			if (/^end/)
			{
				$removeGeneratedCode = 0;
			}
			elsif (/\A\{/)
			{
				$inbody = 1;
			}
			elsif (/\A\}/)
			{
				$inbody = 0;
			}
			elsif (/^sco\s*(\S*)\s*(\S*)/)
			{
				$sco = 1;
				$pctype = "Type";
				$superFieldType = $pctype . ".Field";
				$fieldType = $superFieldType;
				$superscoclass = $1 || "$superclass.Type";
				$supersco = $2 || $superscoclass;
			}
			elsif (/^field((?:\s+.*)?)/) 
			{
				my $options = $1;
				if (($lastLine !~ /^\s*(?:\/\/\s*)?((?:(?:public|private|protected|final|volatile|transient)\s+)*)(\S+)\s+(\w+)/) || ($2 eq "static"))
				{
					die "Instance field declaration expected in $lastLine\n";
				}
				my $name = $3;
				if ($1)
				{
					$options .= " mods=($1)";
				}
				my $ftype = $2;
				my $mtype = $ftype;
				$fieldDecl .= "\tpublic static final $superFieldType " . $name . "\$FIELD;\n";
				$typeId = getTypeId ($ftype);
				if ($options =~ /\btype=(\S+)/)
				{
					$mtype = $1;
				}
				elsif ($typeId == 8)
				{
					my $ft = ($ftype =~ /(\S+)<.+>/) ? $1 : $ftype;
					$mtype = "de.grogra.reflect.ClassAdapter.wrap ($ft.class)";
				}
				if ($mtype =~ /bits\((\S+)\)/)
				{
					my $ftt = $fieldType;
					my $mask = $1;
					$fieldType = "NType.BitField";
					$fieldInit .= "\t\t\$TYPE.addManagedField (${name}\$FIELD = new NType.BitField (\$TYPE, \"$name\","
						. getMods ($options) . ", " . wrapType ($ftype)
						. ", $mask));\n";
					$fieldType = $ftt;
					if ($options =~ /getter/)
					{
						$bitfieldGetterSetter .= "\tpublic boolean is"
							. ucfirst $name
							. " ()\n\t{\n\t\treturn (bits & $mask) != 0;\n\t}\n\n";
					}
					if ($options =~ /setter/)
					{
						$bitfieldGetterSetter .= "\tpublic void set"
							. ucfirst $name
							. " (boolean v)\n\t{\n\t\tif (v) bits |= $mask; else bits &= ~$mask;\n\t}\n\n";
					}
				}
				else
				{
					my $fc;
					if ($sco)
					{
						$fc = $pctype. ".SUPER_FIELD_COUNT + " . $fieldCount;
					}
					else
					{
						$fc = $fieldCount;
					}
					push @{$stack[getTypeId $ftype]}, [$fc, $ftype,
													   $name, $options];
					if ($sco)
					{
						$fieldInit .= "\t\t${name}\$FIELD = $pctype._addManagedField (\$TYPE, ";
					}
					else
					{
						$fieldInit .= "\t\t\$TYPE.addManagedField (";
					}
					if (!$sco)
					{
						$fieldInit .= "${name}\$FIELD = new $fieldType (";
					}
					$fieldInit .= "\"$name\"," . getMods ($options) . ", ";
					if ($options =~ /componenttype=(\S+)/)
					{
						$fieldInit .= wrapType ($mtype) . ", $1, ";
					}
					else
					{
						$fieldInit .= wrapType ($mtype) . ", null, ";
					}
					$fieldInit .= $fc;
					if (!$sco)
					{
						$fieldInit .= ")";
					}
					$fieldInit .= ");\n";
					$fieldCount++;
				}
				if (($options =~ /attr=\{(.*?)\}/) || ($options =~ /attr=(\S+)/))
				{
					$attrDecl .= "\t\t\$TYPE.declareFieldAttribute (${name}\$FIELD, $1);\n";
				}
				if ($options =~ /edge=(\S+)/)
				{
					$attrDecl .= "\t\t\$TYPE.setSpecialEdgeField (${name}\$FIELD, $1);\n";
				}
				if ($options =~ /quantity=(\S+)/)
				{
					$q = $1;
					($q =~ /\./) || ($q = "de.grogra.util.Quantity.$q");
					$attrDecl .= "\t\t${name}\$FIELD.setQuantity ($q);\n";
				}
				if ($options =~ /min=(\S+)/)
				{
					$attrDecl .= "\t\t${name}\$FIELD.setMinValue (new $wrapperNames[$typeId] ($1));\n";
				}
				if ($options =~ /max=(\S+)/)
				{
					$attrDecl .= "\t\t${name}\$FIELD.setMaxValue (new $wrapperNames[$typeId] ($1));\n";
				}
			}
			elsif (/^insert\s*(.*)/)
			{
				if ($1)
				{
					$attrDecl .= "\t\t" . $1 . "\n";
				}
				beginGeneratedCode;
				print "\tpublic static final $pctype \$TYPE;\n\n";
				print $fieldDecl;
				if ($sco)
				{
					print "\n\tpublic static class $pctype extends $superscoclass\n\t{\n";
					print "\t\tpublic $pctype (Class c, de.grogra.persistence.SCOType supertype)\n\t\t{\n\t\t\tsuper (c, supertype);\n\t\t}\n\n";
					print "\t\tpublic $pctype ($class representative, de.grogra.persistence.SCOType supertype)\n\t\t{\n\t\t\tsuper (representative, supertype);\n\t\t}\n\n";
					my $st = ($supersco =~ /(\S+)\.Type/) ? $1 : $supersco;
					print "\t\t$pctype (Class c)\n\t\t{\n\t\t\tsuper (c, $st.\$TYPE);\n\t\t}\n\n";
					if ($fieldCount > 0)
					{
						print "\t\tprivate static final int SUPER_FIELD_COUNT = $superscoclass.FIELD_COUNT;\n";
						print "\t\tprotected static final int FIELD_COUNT = $superscoclass.FIELD_COUNT + $fieldCount;\n";
					}
					print "\n\t\tstatic Field _addManagedField ($pctype t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)\n\t\t{\n\t\t\treturn t.addManagedField (name, modifiers, type, componentType, id);\n\t\t}\n";
					printGetSet (1);
					if ($generateNewInstance)
					{
						print "\n\t\t\@Override";
						print "\n\t\tpublic Object newInstance ()\n\t\t{\n\t\t\treturn new $class ();\n\t\t}\n\n";
					}
					print $body;
					print "\t}\n";
					if ($generateNewInstance)
					{
						print "\n\tpublic de.grogra.persistence.ManageableType getManageableType ()\n\t{\n\t\treturn \$TYPE;\n\t}\n\n";
					}
				}
				elsif (($fieldCount > 0) || $body)
				{
					print "\n\tprivate static final class $fieldType extends $superFieldType\n\t{\n\t\tprivate final int id;\n\n"
						. "\t\t$fieldType (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)\n\t\t{\n"
						. "\t\t\tsuper ($class.\$TYPE, name, modifiers, type, componentType);\n"
						. "\t\t\tthis.id = id;\n\t\t}\n";
					printGetSet (0);
					print $body;
					print "\t}\n";
				}
				print "\n\tstatic\n\t{\n";
				print "\t\t\$TYPE = new $pctype ("
					. ($generateNewInstance && !$sco ? "new $class ()" : "$class.class")
					. ");\n";
				print $fieldInit;
				print $attrDecl;
				print "\t\t\$TYPE.validate ();\n\t}\n\n";
				if (!($sco || $abstract))
				{
					print "\t\@Override\n";
					print "\tprotected $pctype getNTypeImpl ()\n\t{\n\t\treturn \$TYPE;\n\t}\n\n";
					if ($generateNewInstance)
					{
						print "\t\@Override\n";
						print "\tprotected de.grogra.graph.impl.Node newInstance ()\n\t{\n\t\treturn new $class ();\n\t}\n\n";
					}
				}
				printGetterSetter;
				print $bitfieldGetterSetter;
				endGeneratedCode;
			}
		}
		elsif ($inbody)
		{
			$body .= $line;
		}
		$lastLine = $line if !($line =~ /^\s*$/);
	}
	close INPUT;
}


@stack = ([], [], [], [], [], [], [], [], []);
@typeNames = ("boolean", "byte", "short", "char", "int", "long",
			  "float", "double", "Object");
@wrapperNames = ("Boolean", "Byte", "Short", "Character", "Integer", "Long",
				 "Float", "Double", "Object");
if ($ARGV[0] !~ /(.+)\.(\w+)$/)
{
	die "enhance.pl filename.(java|sco) outfile\n";
}
close STDOUT;
open STDOUT, ">", $ARGV[1];
if ($2 eq "java")
{
	enhanceJava $ARGV[0];
}
else
{
	enhanceSCO $ARGV[0];
}
close STDOUT;
